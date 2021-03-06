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
        // some code goes here
    	this.gbfield = gbfield;
    	this.gbfieldtype = gbfieldtype;
    	this.afield = afield;
    	this.what = what;
    	groups = new HashMap<Object, Integer[]>();
    	if (gbfield == NO_GROUPING){
    		 groups.put(what.toString(), new Integer[2]);
    	}
    }

    /**
     * Merge a new tuple into the aggregate, grouping as indicated in the
     * constructor
     *
     * @param tup the Tuple containing an aggregate field and a group-by field
     */
    public void mergeTupleIntoGroup(Tuple tup) {
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
        // some code goes here
        return new IntAggIterator();
    }

}
