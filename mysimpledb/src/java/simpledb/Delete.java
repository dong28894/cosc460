package simpledb;

import java.io.IOException;
import java.util.NoSuchElementException;

/**
 * The delete operator. Delete reads tuples from its child operator and removes
 * them from the table they belong to.
 */
public class Delete extends Operator {

    private static final long serialVersionUID = 1L;
    TransactionId t;
    DbIterator child;
    int fetchCall = 0;
    Tuple returnedTup;

    /**
     * Constructor specifying the transaction that this delete belongs to as
     * well as the child to read from.
     *
     * @param t     The transaction this delete runs in
     * @param child The child operator from which to read tuples for deletion
     */
    public Delete(TransactionId t, DbIterator child) {
        // some code goes here
    	this.t = t;
    	this.child = child;    	
    	Type[] typeArr = {Type.INT_TYPE};
		String[] fieldArr = {""};
		TupleDesc schema = new TupleDesc(typeArr, fieldArr);
		returnedTup = new Tuple(schema);
    }

    public TupleDesc getTupleDesc() {
        // some code goes here
        return returnedTup.getTupleDesc();
    }

    public void open() throws DbException, TransactionAbortedException {
        // some code goes here
    	super.open();
    	child.open();
    }

    public void close() {
        // some code goes here
    	super.close();
    	child.close();
    }

    public void rewind() throws DbException, TransactionAbortedException {
        // some code goes here
    	child.rewind();
    }

    /**
     * Deletes tuples as they are read from the child operator. Deletes are
     * processed via the buffer pool (which can be accessed via the
     * Database.getBufferPool() method.
     *
     * @return A 1-field tuple containing the number of deleted records.
     * @see Database#getBufferPool
     * @see BufferPool#deleteTuple
     */
    protected Tuple fetchNext() throws TransactionAbortedException, DbException {
        // some code goes here
    	if (fetchCall != 0){
    		fetchCall++;
    		return null;
    	}else{
    		fetchCall++;
    		int count = 0;
    		BufferPool pool = Database.getBufferPool();
    		while (child.hasNext()){    			
    			try {
    				Tuple tup = child.next();
					pool.deleteTuple(t, tup);
					count++;
				} catch (NoSuchElementException e) {
					e.printStackTrace();
					throw new DbException("No such element");
				} catch (IOException e) {
					e.printStackTrace();
					throw new DbException("IOException");
				}    			    			
    		}
    		returnedTup.setField(0, new IntField(count));
    		return returnedTup;
    	}
    }

    @Override
    public DbIterator[] getChildren() {
        // some code goes here
    	return new DbIterator[]{child};
    }

    @Override
    public void setChildren(DbIterator[] children) {
        // some code goes here
    	child = children[0];
    }

}
