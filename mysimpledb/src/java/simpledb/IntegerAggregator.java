package simpledb;

import java.util.*;

/**
 * Knows how to compute some aggregate over a set of IntFields.
 */
public class IntegerAggregator implements Aggregator {
	public class IntAggIterator implements DbIterator{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private boolean open;
		private ArrayList<Object> keys;
		private TupleDesc schema;
		private int index = 0;
		public IntAggIterator(){
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
		public boolean hasNext()  {
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
				Integer[] val = groups.get(key);
				if (schema.numFields() == 1){
					IntField f;
					if (what == Op.AVG){
						f = new IntField(val[0].intValue()/val[1].intValue());
					}else{
						f = new IntField(val[0].intValue());
					}
					tup.setField(0, f);
				}else{
					Field f1 = (Field) key;
					IntField f2;					
					if (what == Op.AVG){
						f2 = new IntField(val[0].intValue()/val[1].intValue());
					}else{
						f2 = new IntField(val[0].intValue());
					}
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
    HashMap<Object, Integer[]> groups;

    private Op what;
    private int gbfield;
    private Type gbfieldtype;
    private int afield;
    // a map of groupVal -> AggregateFields
    private HashMap<String, AggregateFields> groups;

    /**
     * Aggregate constructor
     *
     * @param gbfield     the 0-based index of the group-by field in the tuple, or
     *                    NO_GROUPING if there is no grouping
     * @param gbfieldtype the type of the group by field (e.g., Type.INT_TYPE), or null
     *                    if there is no grouping
     * @param afield      the 0-based index of the aggregate field in the tuple
     * @param what        the aggregation operator
     */

    public IntegerAggregator(int gbfield, Type gbfieldtype, int afield, Op what) {
<<<<<<< HEAD
        // some code goes here
    	this.gbfield = gbfield;
    	this.gbfieldtype = gbfieldtype;
    	this.afield = afield;
    	this.what = what;
    	groups = new HashMap<Object, Integer[]>();
    	if (gbfield == NO_GROUPING){
    		 groups.put(what.toString(), new Integer[2]);
    	}
=======
        this.what = what;
        this.gbfield = gbfield;
        this.afield = afield;
        this.gbfieldtype = gbfieldtype;
        this.groups = new HashMap<String, AggregateFields>();
>>>>>>> 215443779cfd9fd9efd8e287aa7079d6a36a225f
    }

    /**
     * Merge a new tuple into the aggregate, grouping as indicated in the
     * constructor
     *
     * @param tup the Tuple containing an aggregate field and a group-by field
     */
    public void mergeTupleIntoGroup(Tuple tup) {
<<<<<<< HEAD
        // some code goes here
    	Integer[] old;
    	Object key;
    	if (gbfield == NO_GROUPING){
    		key = what.toString();
    		old = groups.get(key);
    	}else{ 
    		key = tup.getField(gbfield);
    		if (!groups.containsKey(key)){
    			groups.put(key, new Integer[2]);
    		}
    		old = groups.get(key);
    	}
    	Integer[] update = new Integer[2];
		int newVal = ((IntField)tup.getField(afield)).getValue();
		if (old[0] == null){			
			if (what == Op.AVG){
				update[0] = new Integer(newVal);
				update[1] = new Integer(1);
			}else if (what == Op.COUNT){
				update[0] = new Integer(1);
			}else{
				update[0] = new Integer(newVal);
			}
		}else if (what == Op.AVG){
			update[0] = new Integer(old[0].intValue() + newVal);
			update[1] = new Integer(old[1].intValue() + 1);    			    			
		}else if (what == Op.COUNT){
			update[0] = new Integer(old[0].intValue() + 1);
		}else if (what == Op.MAX){
			if (newVal > old[0].intValue()){
				update[0] = new Integer(newVal);
			}else{
				update = old;
			}
		}else if (what == Op.MIN){
			if (newVal < old[0].intValue()){
				update[0] = new Integer(newVal);
			}else{
				update = old;
			}
		}else{
			update[0] = new Integer(old[0].intValue() + newVal);
		}
		groups.put(key, update);
=======
        String groupVal = "";
        if (gbfield != NO_GROUPING) {
            groupVal = tup.getField(gbfield).toString();
        }
        AggregateFields agg = groups.get(groupVal);
        if (agg == null)
            agg = new AggregateFields(groupVal);

        int x = ((IntField) tup.getField(afield)).getValue();

        agg.count++;
        agg.sum += x;
        agg.min = (x < agg.min ? x : agg.min);
        agg.max = (x > agg.max ? x : agg.max);
        if (what == Op.SC_AVG)
            agg.sumCount += ((IntField) tup.getField(afield + 1)).getValue();

        groups.put(groupVal, agg);
>>>>>>> 215443779cfd9fd9efd8e287aa7079d6a36a225f
    }

    /**
     * Create a DbIterator over group aggregate results.
     *
     * @return a DbIterator whose tuples are the pair (groupVal, aggregateVal)
     * if using group, or a single (aggregateVal) if no grouping. The
     * aggregateVal is determined by the type of aggregate specified in
     * the constructor.
     */
    public DbIterator iterator() {
<<<<<<< HEAD
        // some code goes here
        return new IntAggIterator();                                   
=======
        LinkedList<Tuple> result = new LinkedList<Tuple>();
        int aggField = 1;
        TupleDesc td;

        if (gbfield == NO_GROUPING) {
            if (what == Op.SUM_COUNT)
                td = new TupleDesc(new Type[]{Type.INT_TYPE, Type.INT_TYPE});
            else
                td = new TupleDesc(new Type[]{Type.INT_TYPE});
            aggField = 0;
        } else {
            if (what == Op.SUM_COUNT)
                td = new TupleDesc(new Type[]{gbfieldtype, Type.INT_TYPE, Type.INT_TYPE});
            else
                td = new TupleDesc(new Type[]{gbfieldtype, Type.INT_TYPE});
        }

        // iterate over groups and create summary tuples
        for (String groupVal : groups.keySet()) {
            AggregateFields agg = groups.get(groupVal);
            Tuple tup = new Tuple(td);

            if (gbfield != NO_GROUPING) {
                if (gbfieldtype == Type.INT_TYPE)
                    tup.setField(0, new IntField(new Integer(groupVal)));
                else
                    tup.setField(0, new StringField(groupVal, Type.STRING_LEN));
            }
            switch (what) {
                case MIN:
                    tup.setField(aggField, new IntField(agg.min));
                    break;
                case MAX:
                    tup.setField(aggField, new IntField(agg.max));
                    break;
                case SUM:
                    tup.setField(aggField, new IntField(agg.sum));
                    break;
                case COUNT:
                    tup.setField(aggField, new IntField(agg.count));
                    break;
                case AVG:
                    tup.setField(aggField, new IntField(agg.sum / agg.count));
                    break;
                case SUM_COUNT:
                    tup.setField(aggField, new IntField(agg.sum));
                    tup.setField(aggField + 1, new IntField(agg.count));
                    break;
                case SC_AVG:
                    tup.setField(aggField, new IntField(agg.sum / agg.sumCount));
                    break;
            }

            result.add(tup);
        }

        DbIterator retVal = null;
        retVal = new TupleIterator(td, Collections.unmodifiableList(result));
        return retVal;
    }

    /**
     * A helper struct to store accumulated aggregate values.
     */
    private class AggregateFields {
        public String groupVal;
        public int min, max, sum, count, sumCount;

        public AggregateFields(String groupVal) {
            this.groupVal = groupVal;
            min = Integer.MAX_VALUE;
            max = Integer.MIN_VALUE;
            sum = count = sumCount = 0;
        }
>>>>>>> 215443779cfd9fd9efd8e287aa7079d6a36a225f
    }

}
