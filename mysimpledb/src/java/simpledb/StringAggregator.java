package simpledb;

<<<<<<< HEAD
import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Set;
=======
import java.util.*;
>>>>>>> 215443779cfd9fd9efd8e287aa7079d6a36a225f

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
<<<<<<< HEAD
    int gbfield;
    Type gbfieldtype;
    int afield;
    Op what;
    HashMap<Object, Integer> groups;
    int count;
=======
    private Op what;
    private int gbfield;
    private Type gbfieldtype;
    private int afield;
    // a map of groupVal -> AggregateFields
    private HashMap<String, AggregateFields> groups;
>>>>>>> 215443779cfd9fd9efd8e287aa7079d6a36a225f

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
<<<<<<< HEAD
        // some code goes here
    	this.gbfield = gbfield;
    	this.gbfieldtype = gbfieldtype;
    	this.afield = afield;
    	this.what = what;
    	groups = new HashMap<Object, Integer>();
    	if (what != Op.COUNT){
    		throw new IllegalArgumentException();
    	}
=======
        this.what = what;
        if (what != Op.COUNT)
            throw new IllegalArgumentException("Invalid operator type " + what);
        this.gbfield = gbfield;
        this.afield = afield;
        this.gbfieldtype = gbfieldtype;
        this.groups = new HashMap<String, AggregateFields>();
>>>>>>> 215443779cfd9fd9efd8e287aa7079d6a36a225f
    }

    /**
     * Merge a new tuple into the aggregate, grouping as indicated in the constructor
     *
     * @param tup the Tuple containing an aggregate field and a group-by field
     */
    public void mergeTupleIntoGroup(Tuple tup) {
<<<<<<< HEAD
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
=======
        String groupVal = "";
        if (gbfield != NO_GROUPING) {
            groupVal = tup.getField(gbfield).toString();
        }
        AggregateFields agg = groups.get(groupVal);
        if (agg == null)
            agg = new AggregateFields(groupVal);

        agg.count++;

        groups.put(groupVal, agg);
>>>>>>> 215443779cfd9fd9efd8e287aa7079d6a36a225f
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
<<<<<<< HEAD
        // some code goes here
        return new StrAggIterator();
=======
        LinkedList<Tuple> result = new LinkedList<Tuple>();
        int aggField = 1;
        TupleDesc td;

        if (gbfield == NO_GROUPING) {
            td = new TupleDesc(new Type[]{Type.INT_TYPE});
            aggField = 0;
        } else {
            td = new TupleDesc(new Type[]{gbfieldtype, Type.INT_TYPE});
        }

        // iterate over groups and create summary tuples
        for (String groupVal : groups.keySet()) {
            AggregateFields agg = groups.get(groupVal);
            Tuple tup = new Tuple(td);

            if (gbfield != NO_GROUPING) {
                if (gbfieldtype == Type.INT_TYPE)
                    tup.setField(0, new IntField(new Integer(groupVal)));
                else tup.setField(0, new StringField(groupVal, Type.STRING_LEN));
            }

            switch (what) {
                case COUNT:
                    tup.setField(aggField, new IntField(agg.count));
                    break;
            }

            result.add(tup);
        }

        DbIterator retVal = null;
        retVal = new TupleIterator(td, Collections.unmodifiableList(result));
        return retVal;
>>>>>>> 215443779cfd9fd9efd8e287aa7079d6a36a225f
    }

    /**
     * A helper struct to store accumulated aggregate values.
     */
    private class AggregateFields {
        public String groupVal;
        public int count;

        public AggregateFields(String groupVal) {
            this.groupVal = groupVal;
            count = 0;
        }
    }
}
