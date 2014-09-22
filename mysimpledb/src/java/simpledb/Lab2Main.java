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

        //Update table
        TransactionId tid = new TransactionId();
        SeqScan f = new SeqScan(tid, table1.getId());

        try {            
            f.open();
            while (f.hasNext()) {
                Tuple tup = f.next();                
                if (((IntField)tup.getField(1)).getValue() < 3){
                	System.out.println("Update tuple: ");
                	System.out.println(tup);
                	Database.getBufferPool().deleteTuple(tid, tup);
                	tup.setField(1, new IntField(3));
                	System.out.println(" to be: ");
                	System.out.println(tup);
                	Database.getBufferPool().insertTuple(tid, table1.getId(), tup);
                }
            }
            Tuple newTup = new Tuple(f.getTupleDesc());
            newTup.setField(0, new IntField(99));
            newTup.setField(1, new IntField(99));
            newTup.setField(2, new IntField(99));
            System.out.println("insertTuple: ");
            System.out.println(newTup);
            Database.getBufferPool().insertTuple(tid, table1.getId(), newTup);            
            Database.getBufferPool().flushAllPages();
            f.close();
            Database.getBufferPool().transactionComplete(tid);
        } catch (Exception e) {
            System.out.println ("Exception : " + e);
        }
        //Print table
        System.out.println("The table now contains the following records:");
        TransactionId tid2 = new TransactionId();
        SeqScan f2 = new SeqScan(tid2, table1.getId());

        try {
            f2.open();
            while (f2.hasNext()) {
                Tuple tup = f2.next();
                System.out.println(tup);
            }
            f2.close();
            Database.getBufferPool().transactionComplete(tid2);
        } catch (Exception e) {
            System.out.println ("Exception : " + e);
        }
    }

}