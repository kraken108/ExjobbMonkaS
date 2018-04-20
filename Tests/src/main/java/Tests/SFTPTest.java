package Tests;

import Exceptions.SFTPClientException;
import SFTPLogic.SFTPClient;
import SFTPLogic.SFTPDataGenerator;
import Tests.FileHandler.CsvWriter;
import Tests.Measuring.MeasureResult;

import java.util.ArrayList;

public class SFTPTest extends TestI{

    private String folderName;

    public SFTPTest(String folderName){

        this.folderName = folderName;
    }

    @Override
    void testSendTransactions(int amountOfTransactions) {
        System.out.println("SFTP test: send transactions (" + amountOfTransactions + ")");
        super.setFileNameEnding("SFTPSendManyTrans"+ amountOfTransactions);

        System.gc();
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ArrayList<MeasureResult> results = new ArrayList<>();
        for(int i = 0; i < 20; i++){ // do test 20 times and calculate avg
            /** ONE LIFECYCLE **/
            SFTPDataGenerator dg = new SFTPDataGenerator();
            String fname = dg.generateTransactionBatch(amountOfTransactions);
            SFTPClient sftpc =
                    new SFTPClient(ServerInfo.getServerIp(),22, ServerInfo.getSFTPUsername(),
                            ServerInfo.getSFTPPassword(),2222);
            try {
                AverageMeasurement am = new AverageMeasurement(folderName,filename);
                Thread t = new Thread(am);
                am.setIsRunning(true);
                t.start();
                sftpc.connect();
                sftpc.uploadFile("/Users/do/IdeaProjects/ExjobbMonkaSrevert/Hejhej/"+fname,
                        "/Users/do/Documents/REQUESTDOCUMENTS/"+fname);
                sftpc.disconnect();
                am.setIsRunning(false);
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                results.add(am.getResult());

            } catch (SFTPClientException e) {
                e.printStackTrace();
            }
            /*******************/
            //clear db
            super.clearDatabase();
        }


            //write to file
            new CsvWriter().writeToFile(folderName + filename, super.getAverageResult(results));
            System.out.println("DONE WITH SFTP SEND TRANSACTIONS TEST");




    }

    @Override
    void testRetrieveTransactions(int amountOfTransactions) {
        System.out.println("SFTP test: retrieve transactions (" + amountOfTransactions + ")");
        super.setFileNameEnding("SFTPRetrieveManyTrans"+amountOfTransactions);

        System.gc();
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ArrayList<MeasureResult> results = new ArrayList<>();
        for(int i = 0; i < 20; i++){
            /** ONE LIFECYCLE **/
            SFTPClient sftpc = new SFTPClient(ServerInfo.getServerIp(),22,ServerInfo.getSFTPUsername(),ServerInfo.getSFTPPassword(),2222);
            try {
                AverageMeasurement am = new AverageMeasurement(folderName,filename);
                Thread t = new Thread(am);
                am.setIsRunning(true);
                t.start();
                sftpc.connect();
                sftpc.retrieveFile("dag",amountOfTransactions);
                sftpc.disconnect();

                am.setIsRunning(false);
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                results.add(am.getResult());
            } catch (SFTPClientException e) {
                System.out.println("Connection failed, quitting");
                return;
            }
            /******************/
            //clear db
            super.clearDatabase();
        }


            //write to file
            new CsvWriter().writeToFile(folderName + filename, super.getAverageResult(results));
            System.out.println("DONE WITH SFTP RETRIEVE TRANSACTIONS TEST");



    }


    @Override
    void setUpEnvironment() {

    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
