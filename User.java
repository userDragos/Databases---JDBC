import java.sql.*;
import java.sql.DatabaseMetaData;
import java.util.*;
import java.lang.*;


public class User extends DbBasic { 
    public User (String dbName){
		super(dbName);
	}

    private DatabaseMetaData metadata = null;
    private ArrayList<String> tableNames = new ArrayList<String>();
    private String query;
    private ArrayList<String> insertColl = new ArrayList<String>();
    private ArrayList<String> primary = new ArrayList<String>();
    public void printTables(){
        try{
            metadata = con.getMetaData();
            ResultSet gt = metadata.getTables(null,null,null,new String[]{"TABLE"});
            //fetch all the tables from the database 
            while(gt.next()){
                tableNames.add(gt.getString("TABLE_NAME"));
            }
            
            for(String s : tableNames ){
                //for each table that exists in the database get the column and print it
                System.out.print("CREATE TABLE " + s + " (");
                ResultSet gc = metadata.getColumns(null,null,s,null);
                while(gc.next()){
                    System.out.print(gc.getString("COLUMN_NAME")+" ");
                    System.out.print(gc.getString("TYPE_NAME"));

                    System.out.print(", "); 
                }
                ResultSet primaryKeys = metadata.getPrimaryKeys(null,null,s);
                
                System.out.print("primary key (");
                //get all the primary keys for the table , add them to a String array in order to find how many there are
                while(primaryKeys.next()){
                    primary.add(primaryKeys.getString("COLUMN_NAME"));
                }
                int x=0;
                //for each primary key in the array print them, if it is the last one do not print a comma at the end 
                for(String p : primary){
                    System.out.print(p);
                    if(x<primary.size()-1){
                        System.out.print(", ");
                    }
                    x++;   
                }
                //clear the array list otherwise it will always add more primary keys into the array
                primary.clear();
                System.out.print(")");
                ResultSet foreignKeys = metadata.getImportedKeys(null,null,s);
                //fetch the foreign keys and print them to the screen
                while(foreignKeys.next()){
                    System.out.print(", foreign key (" + foreignKeys.getString("FKCOLUMN_NAME")+")");
                    System.out.print(" references "+ foreignKeys.getString("PKTABLE_NAME"));
                    System.out.print("("+foreignKeys.getString("PKCOLUMN_NAME")+")");
                }
                System.out.println(");");
            }
        }
        catch(SQLException e){
            System.out.println(e);
        }    
    } 
    public void printInserts(){
        try{
            
            metadata = con.getMetaData();
            Statement statement = con.createStatement();
            for (String s : tableNames){
                query = "SELECT * FROM "+ s ;
                ResultSet execute = statement.executeQuery(query);
                //execute the query written above 
                while(execute.next()){
                    ResultSet column = metadata.getColumns(null, null, s, null); 
                    System.out.print("INSERT INTO " + s+" VALUES(");
                    //fetch the columns and print them
                    while(column.next()){
                        String col = column.getString("COLUMN_NAME");
                        insertColl.add(execute.getString(col));
                    }
                    int i = 0;
                    //if the current database contains the character ' it will be replaced with " because otherwise the sql will not recognise it as being a string but as a end of statement
                    for(String g: insertColl){
                        if(g.contains("'")){
                            g = g.replace("'","\"");
                        }
                        System.out.print("'"+g+"'");
                        if(i!=insertColl.size()-1){
                            System.out.print(", ");
                        }
                        i++;
                    }
                    insertColl.clear();
                    System.out.println(");");
                }
                System.out.println("");
            }
        }
        catch(SQLException e){
            System.out.println(e);
        }
    }  

    public void printIndex(){
        try{
            metadata = con.getMetaData();
            for(String s:tableNames){
                ResultSet index = metadata.getIndexInfo(null,null,s,false,true);
                ResultSet coll = metadata.getColumns(null,null,s,null);
                //create a list to calculate the number of columns 
                while(coll.next()){
                    insertColl.add("Empty");
                }
                //print only the indexes that are custom and not the ones generated by sql
                while(index.next()){
                    String indexName = index.getString("INDEX_NAME");
                    if(indexName.contains("sqlite_autoindex")){
                       
                    }
                    else{
                        System.out.print("CREATE UNIQUE INDEX '");
                        System.out.println(indexName+"' ON '"+index.getString("TABLE_NAME") +"'('"+index.getString("COLUMN_NAME")+"');");
                    }
                }
            }
        }
        catch(SQLException e){
            System.out.println(e);
        } 
    }
}