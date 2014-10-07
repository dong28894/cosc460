package simpledb;

import java.io.IOException;
import java.util.ArrayList;

public class Lab3Main {

    public static void main(String[] argv) 
       throws DbException, TransactionAbortedException, IOException {

        System.out.println("Loading schema from file:");
        // file named college.schema must be in mysimpledb directory
        Database.getCatalog().loadSchema("college.schema");

        TransactionId tid = new TransactionId();
        //Scan students(S) table
        SeqScan scanStudents = new SeqScan(tid, Database.getCatalog().getTableId("students"));
        //Scan takes(T) table
        SeqScan scanTakes = new SeqScan(tid, Database.getCatalog().getTableId("takes"));
        //Scan profs(P) table
        SeqScan scanProfs = new SeqScan(tid, Database.getCatalog().getTableId("profs"));
        //P.name = "hay"
        Filter profHay = new Filter(new Predicate(1, Predicate.Op.EQUALS, new StringField("hay", 12)), scanProfs);
        //T.cid = P.favoriteCourse
        JoinPredicate p1 = new JoinPredicate(1, Predicate.Op.EQUALS, 2);
        Join profTake = new Join(p1, scanTakes, profHay);
        //S.sid = T.sid
        JoinPredicate p2 = new JoinPredicate(0, Predicate.Op.EQUALS, 0);
        Join studentTake = new Join(p2, scanStudents, profTake);
        //Project S.name
        ArrayList<Integer> fieldList = new ArrayList<Integer>();
        fieldList.add(new Integer(1));
        Project result = new Project(fieldList, new Type[]{Type.STRING_TYPE}, studentTake);

        // query execution: we open the iterator of the root and iterate through results
        System.out.println("Query results:");
        result.open();
        while (result.hasNext()) {
            Tuple tup = result.next();
            System.out.println("\t"+tup);
        }
        result.close();
        Database.getBufferPool().transactionComplete(tid);
    }

}
