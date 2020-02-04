package me.tinnan.test.jt400.cli;

import com.ibm.as400.access.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.beans.PropertyVetoException;
import java.io.IOException;

@ShellComponent
public class LogonAs400Command {

    @Value("${as400.host}")
    private String host;

    @Value("${as400.logon.programname}")
    private String logonProgramName;

    @ShellMethod("Logon AS400 command.")
    public String logon(String userid, String password, String logonuser) {
        System.out.println("host :: " + host);
        System.out.println("Program name :: " + logonProgramName);
        AS400 as400 = null;
        ProgramParameter[] paramList = new ProgramParameter[2];
        byte[] inputData;
        byte[] outputData;
        ProgramCall programCall;
        int programTimeoutSec = 5;

        try {
            System.out.println("Create AS400 object with host :: " + this.host);
            System.out.println("Authenticate with user :: " + userid);
            as400 = new AS400(this.host, userid, password);

            // Convert the Strings to IBM format
            inputData = logonuser.getBytes("IBM285");

            // Create the input parameter
            paramList[0] = new ProgramParameter(inputData);

            // Create the output parameter
            //Prarameterised Constructor is for the OUTPUT LENGTH. here it is 10
            paramList[1] = new ProgramParameter(10);

            /**
             * Create a program object specifying the name of the program and
             * the parameter list.
             */
            programCall = new ProgramCall(as400);
            programCall.setProgram(logonProgramName, paramList);
            System.out.println("Set program timeout (second) :: " + programTimeoutSec);
            programCall.setTimeOut(5);

            // Run the program.
            System.out.println("Call program.");
            if (!programCall.run()) {
                /**
                 * If the AS/400 is not run then look at the message list to
                 * find out why it didn't run.
                 */
                AS400Message[] messageList = programCall.getMessageList();
                for (AS400Message message : messageList) {
                    System.out.println(message.getID() + " - " + message.getText());
                }

                return "Program call failed.";
            } else {
                /**
                 * Else the program is successfull. Process the output, which
                 * contains the returned data.
                 */
                System.out.println("CONNECTION IS SUCCESSFUL");
                outputData = paramList[1].getOutputData();
                String output = new String(outputData, "IBM285").trim();

                return "Output is " + output;
            }

        } catch (PropertyVetoException | AS400SecurityException
                | ErrorCompletingRequestException | IOException
                | InterruptedException | ObjectDoesNotExistException e) {
            return ":: Exception ::" + e.toString();
        } finally {
            try {
                // Make sure to disconnect
                if (as400 != null) {
                    as400.disconnectAllServices();
                }
            } catch (Exception e) {
                System.err.println(":: Exception ::" + e.toString());
            }
        }
    }
}
