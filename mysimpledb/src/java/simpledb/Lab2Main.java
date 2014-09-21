package simpledb;
import java.io.*;

public class Lab2Main {

    public static void main(String[] argv) {

        // construct a 3-column table schema
        Type types[] = new Type[]{ Type.INT_TYPE, Type.INT_TYPE, Type.INT_TYPE };
        String names[] = new String[]{ "field0", "field1", "field2" };
        TupleDesc descriptor = new TupleDesc(types, names);

        // create the table, associate it with some_data_file.dat
        // and tell the catalog about the schema of this table.
        HeapFile table1 = new HeapFile(new File("some_data_file.dat"), descriptor);
        Database.getCatalog().addTable(table1, "test");

        // construct the query: we use a simple SeqScan, which spoonfeeds
        // tuples via its iterator.
        TransactionId tid = new TransactionId();
        SeqScan f = new SeqScan(tid, table1.getId());

        try {
            // and run it
            f.open();
            while (f.hasNext()) {
                Tuple tup = f.next();
                System.out.println(tup);
                if (((IntField)tup.getField(1)).getValue() < 3){
                	Database.getBufferPool().deleteTuple(tid, tup);
                	tup.setField(1, new IntField(3));
                	System.out.println(tup);
                	Database.getBufferPool().insertTuple(tid, table1.getId(), tup);
                }
                System.out.println(tup);
            }
            Tuple newTup = new Tuple(f.getTupleDesc());
            newTup.setField(0, new IntField(99));
            newTup.setField(1, new IntField(99));
            newTup.setField(2, new IntField(99));
            Database.getBufferPool().insertTuple(tid, table1.getId(), newTup);
            System.out.println(newTup);
            Database.getBufferPool().flushAllPages();
            f.close();
            Database.getBufferPool().transactionComplete(tid);
        } catch (Exception e) {
            System.out.println ("Exception : " + e);
        }
    }

}