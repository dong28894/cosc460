package simpledb;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TableStats represents statistics (e.g., histograms) about base tables in a
 * query.
 * <p/>
 * This class is not needed in implementing lab1|lab2|lab3.                                                   // cosc460
 */
public class TableStats {

    private static final ConcurrentHashMap<String, TableStats> statsMap = new ConcurrentHashMap<String, TableStats>();

    static final int IOCOSTPERPAGE = 1000;

    public static TableStats getTableStats(String tablename) {
        return statsMap.get(tablename);
    }

    public static void setTableStats(String tablename, TableStats stats) {
        statsMap.put(tablename, stats);
    }

    public static void setStatsMap(HashMap<String, TableStats> s) {
        try {
            java.lang.reflect.Field statsMapF = TableStats.class.getDeclaredField("statsMap");
            statsMapF.setAccessible(true);
            statsMapF.set(null, s);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    public static Map<String, TableStats> getStatsMap() {
        return statsMap;
    }

    public static void computeStatistics() {
        Iterator<Integer> tableIt = Database.getCatalog().tableIdIterator();

        System.out.println("Computing table stats.");
        while (tableIt.hasNext()) {
            int tableid = tableIt.next();
            TableStats s = new TableStats(tableid, IOCOSTPERPAGE);
            setTableStats(Database.getCatalog().getTableName(tableid), s);
        }
        System.out.println("Done.");
    }

    /**
     * Number of bins for the histogram. Feel free to increase this value over
     * 100, though our tests assume that you have at least 100 bins in your
     * histograms.
     */
    static final int NUM_HIST_BINS = 100;
    int scanCost;
    int numTups;
    Object[] hist;
    int[] numDistinctVals;

    /**
     * Create a new TableStats object, that keeps track of statistics on each
     * column of a table
     *
     * @param tableid       The table over which to compute statistics
     * @param ioCostPerPage The cost per page of IO. This doesn't differentiate between
     *                      sequential-scan IO and disk seeks.
     */
    public TableStats(int tableid, int ioCostPerPage) {
        // For this function, you'll have to get the
        // DbFile for the table in question,
        // then scan through its tuples and calculate
        // the values that you need.
        // You should try to do this reasonably efficiently, but you don't
        // necessarily have to (for example) do everything
        // in a single scan of the table.
        // some code goes here
    	HeapFile table = (HeapFile) Database.getCatalog().getDatabaseFile(tableid);
    	TupleDesc schema = table.getTupleDesc();
    	hist = new Object[schema.numFields()];
    	int[] max = new int[schema.numFields()];
    	int[] min = new int[schema.numFields()];
    	Arrays.fill(max, Integer.MIN_VALUE);
    	Arrays.fill(min, Integer.MAX_VALUE);
    	int numPages = table.numPages();
    	scanCost = numPages*ioCostPerPage;
    	TransactionId tid = new TransactionId();
    	numTups = 0;
    	numDistinctVals = new int[schema.numFields()];
    	ArrayList<HashSet<Object>> distinctVals = new ArrayList<HashSet<Object>>();
    	for (int i = 0; i < schema.numFields(); i++){
    		distinctVals.add(new HashSet<Object>());
    	}
    	DbFileIterator iter = table.iterator(tid);   		
    	try {
    		iter.open();
			while (iter.hasNext()){
				Tuple curr = iter.next();
				numTups++;
				for (int i = 0; i < hist.length; i++){    			
					if (schema.getFieldType(i) == Type.INT_TYPE){
						int currVal = ((IntField)curr.getField(i)).getValue();
						distinctVals.get(i).add(new Integer(currVal));
						if (currVal > max[i]){
							max[i] = currVal;
						}
						if (currVal < min[i]){
							min[i] = currVal;
						}
					}else{
						String currVal = ((StringField)curr.getField(i)).getValue();
						distinctVals.get(i).add(new String(currVal));
					}
				}
			}			
		} catch (NoSuchElementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransactionAbortedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	for (int i = 0; i < hist.length; i++){
    		if (schema.getFieldType(i) == Type.INT_TYPE){
    			hist[i] = new IntHistogram(NUM_HIST_BINS, min[i], max[i]);    		
    		}else{
    			hist[i] = new StringHistogram(NUM_HIST_BINS);
    		}
    		numDistinctVals[i] = distinctVals.get(i).size();
    	}
    	try {
			iter.rewind();
			while (iter.hasNext()){
	    		Tuple curr = iter.next();
	    		for (int i = 0; i < hist.length; i++){    			
					if (schema.getFieldType(i) == Type.INT_TYPE){
						int currVal = ((IntField)curr.getField(i)).getValue();
						((IntHistogram)hist[i]).addValue(currVal);
					}else{
						String currVal = ((StringField)curr.getField(i)).getValue();
						((StringHistogram)hist[i]).addValue(currVal);
					}
				}
	    	}
			iter.close();
			Database.getBufferPool().transactionComplete(tid);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransactionAbortedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    	
    }

    /**
     * Estimates the cost of sequentially scanning the file, given that the cost
     * to read a page is costPerPageIO. You can assume that there are no seeks
     * and that no pages are in the buffer pool.
     * <p/>
     * Also, assume that your hard drive can only read entire pages at once, so
     * if the last page of the table only has one tuple on it, it's just as
     * expensive to read as a full page. (Most real hard drives can't
     * efficiently address regions smaller than a page at a time.)
     *
     * @return The estimated cost of scanning the table.
     */
    public double estimateScanCost() {
        // some code goes here
        return scanCost;
    }

    /**
     * This method returns the number of tuples in the relation, given that a
     * predicate with selectivity selectivityFactor is applied.
     *
     * @param selectivityFactor The selectivity of any predicates over the table
     * @return The estimated cardinality of the scan with the specified
     * selectivityFactor
     */
    public int estimateTableCardinality(double selectivityFactor) {
        // some code goes here
        return (int) Math.ceil(numTups*selectivityFactor);
    }

    /**
     * This method returns the number of distinct values for a given field.
     * If the field is a primary key of the table, then the number of distinct
     * values is equal to the number of tuples.  If the field is not a primary key
     * then this must be explicitly calculated.  Note: these calculations should
     * be done once in the constructor and not each time this method is called. In
     * addition, it should only require space linear in the number of distinct values
     * which may be much less than the number of values.
     *
     * @param field the index of the field
     * @return The number of distinct values of the field.
     */
    public int numDistinctValues(int field) {
        // some code goes here
        return numDistinctVals[field];

    }

    /**
     * Estimate the selectivity of predicate <tt>field op constant</tt> on the
     * table.
     *
     * @param field    The field over which the predicate ranges
     * @param op       The logical operation in the predicate
     * @param constant The value against which the field is compared
     * @return The estimated selectivity (fraction of tuples that satisfy) the
     * predicate
     */
    public double estimateSelectivity(int field, Predicate.Op op, Field constant) {
        // some code goes here
        if (constant.getType() == Type.INT_TYPE){
        	IntHistogram currFieldHist = (IntHistogram) hist[field];
        	return currFieldHist.estimateSelectivity(op, ((IntField)constant).getValue());
        }else{
        	StringHistogram currFieldHist = (StringHistogram) hist[field];
        	return currFieldHist.estimateSelectivity(op, ((StringField)constant).getValue());
        }
    }

}
