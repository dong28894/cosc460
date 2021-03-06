package simpledb;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Knows how to compute some aggregate over a set of StringFields.
 */
public class StringAggregator implements Aggregator {
	public class StrAggIterator implements DbIterator{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private boolean open;
		private TupleDesc schema;
		private ArrayList<Object> keys;
		private int index = 0;
		public StrAggIterator(){
			open = false;
			Set<Object> keySet = groups.keySet();
			keys = new ArrayList<Object>();
			for (Object o: keySet){
				keys.add(o);
			}
			if (gbfield == NO_GROUPING){
				schema = new TupleDesc(new Type[]{Type.INT_TYPE}, new String[]{"aggregateValue"});
			}else{
				schema = new TupleDesc(new Type[]{gbfieldtype, Type.INT_TYPE}, new String[]{"groupValue","aggregateValue"});
			}
		}
		public void open() {
			open = true;
		}

		public boolean hasNext(){
			if (open){
				if (index < keys.size()){
					return true;
				}
			}
			return false;
		}

		public Tuple next() throws NoSuchElementException {
			if (hasNext()){
				Tuple tup = new Tuple(schema);
				Object key = keys.get(index);
				Integer val = groups.get(key);
				if (schema.numFields() == 1){
					IntField f = new IntField(val.intValue());
					tup.setField(0, f);
				}else{
					Field f1 = (Field) key;
					IntField f2 = new IntField(val.intValue());
					tup.setField(0, f1);
					tup.setField(1, f2);
				}
				index++;
				return tup;
			}
			throw new NoSuchElementException();
		}

		public void rewind() {
			index = 0;			
		}

		public TupleDesc getTupleDesc() {
			return schema;
		}

		public void close() {
			open = false;
		}
		
	}
    private static final long serialVersionUID = 1L;
    int gbfield;
    Type gbfieldtype;
    int afield;
    Op what;
    HashMap<Object, Integer> groups;
    int count;

    /**
     * Aggregate constructor
     *
     * @param gbfield     the 0-based index of the group-by field in the tuple, or NO_GROUPING if there is no grouping
     * @param gbfieldtype the type of the group by field (e.g., Type.INT_TYPE), or null if there is no grouping
     * @param afield      the 0-based index of the aggregate field in the tuple
     * @param what        aggregation operator to use -- only supports COUNT
     * @throws IllegalArgumentException if what != COUNT
     */

    public StringAggregator(int gbfield, Type gbfieldtype, int afield, Op what) {
        // some code goes here
    	this.gbfield = gbfield;
    	this.gbfieldtype = gbfieldtype;
    	this.afield = afield;
    	this.what = what;
    	groups = new HashMap<Object, Integer>();
    	if (what != Op.COUNT){
    		throw new IllegalArgumentException();
    	}
    }

    /**
     * Merge a new tuple into the aggregate, grouping as indicated in the constructor
     *
     * @param tup the Tuple containing an aggregate field and a group-by field
     */
    public void mergeTupleIntoGroup(Tuple tup) {
        // some code goes here
    	if (gbfield == NO_GROUPING){
    		count++;
    	}else{
    		Field key = tup.getField(gbfield);
    		if (!groups.containsKey(key)){
    			groups.put(key, new Integer(1));
    		}else{
    			groups.put(key, new Integer(groups.get(key).intValue()+1));
    		}    		
    	}
    }

    /**
     * Create a DbIterator over group aggregate results.
     *
     * @return a DbIterator whose tuples are the pair (groupVal,
     * aggregateVal) if using group, or a single (aggregateVal) if no
     * grouping. The aggregateVal is determined by the type of
     * aggregate specified in the constructor.
     */
    public DbIterator iterator() {
        // some code goes here
        return new StrAggIterator();
    }    
}
