package snmp;
/*
 * Copyright (c) 2002 iReasoning Networks. All Rights Reserved.
 * 
 * This SOURCE CODE FILE, which has been provided by iReasoning Networks as part
 * of an iReasoning Software product for use ONLY by licensed users of the product,
 * includes CONFIDENTIAL and PROPRIETARY information of iReasoning Networks.  
 *
 * USE OF THIS SOFTWARE IS GOVERNED BY THE TERMS AND CONDITIONS 
 * OF THE LICENSE STATEMENT AND LIMITED WARRANTY FURNISHED WITH
 * THE PRODUCT.
 *
 * IN PARTICULAR, YOU WILL INDEMNIFY AND HOLD IREASONING SOFTWARE, ITS
 * RELATED COMPANIES AND ITS SUPPLIERS, HARMLESS FROM AND AGAINST ANY
 * CLAIMS OR LIABILITIES ARISING OUT OF THE USE, REPRODUCTION, OR
 * DISTRIBUTION OF YOUR PROGRAMS, INCLUDING ANY CLAIMS OR LIABILITIES
 * ARISING OUT OF OR RESULTING FROM THE USE, MODIFICATION, OR
 * DISTRIBUTION OF PROGRAMS OR FILES CREATED FROM, BASED ON, AND/OR
 * DERIVED FROM THIS SOURCE CODE FILE.
 */


import java.io.*;

import com.ireasoning.protocol.Session;
import com.ireasoning.protocol.snmp.*;
import com.ireasoning.util.ParseArguments;

/**
 * This class is the base class of snmp command line programs. It provides
 * useful functionality such as parsing command line arguments, prints out
 * PDU data, etc.
 * <br>
 * Note: MIB-II is preloaded.
 */
public abstract class SNMP 
{
    private static final String DEFAULT_HOST = "192.168.10.1";

	protected ParseArguments _parseArgs;
    
    protected int         _port;
    protected int         _version;
    protected String      _host;
    protected String      _community;
    protected int         _transportLayer;
    
    /**
     * Prints out usage
     */
    protected void printUsage()
    {
        usage();
        
        System.out.println( "\nwhere possible options include:");
        System.out.println( "-?\tprint this Usage");
        System.out.println( "-h\tprint this Usage");
        System.out.println( "-c <c>\tcommunity name");
        System.out.println( "-v <1|2>\tspecifies snmp version to use, default is 1 (snmpV3 not supported)");
        System.out.println( "-p <p>\tport number");
        System.out.println( "-k <UDP|TCP>\ttransport layer, possible values are UDP or TCP");
              
        System.out.println( "\nExample:");
        printExample();
    }

    /**
     * Prints usage lines without explanation lines
     */
    protected void usage()
    {
    	System.out.println( "Usage: java NetworkSocketViewer [options...] <hostname> \n");
    }
    
   
    /**
     * Prints out the example usage
     */
    protected void printExample()
    {
        System.out.println( "java NetworkSocketViewer 192.168.20.1");
        System.out.println( "java NetworkSocketViewer localhost -v 2");
    }
    
    /**
     * Prints SnmpPdu content to standard out
     */
    protected void print(SnmpPdu pdu)
    {
        print(pdu, System.out);    
    }
    
    /**
     * Prints SnmpPdu content to PrintStream p
     */
    protected void print(SnmpPdu pdu, PrintStream p)
    {
        if(pdu.getErrorStatus() > 0)
        {//Error occurs or end of mib view reached.
            System.out.println( "PDU error status = " + pdu.getErrorStatusString());
            return;
        }
        if(pdu.isSnmpV3AuthenticationFailed())
        {
            System.out.println( "Authentication failure. Reason:");
            printAuthFailReason(pdu);
            return;
        }
        print(pdu.getVarBinds(), p);
    }
    private void printAuthFailReason(SnmpPdu pdu)
    {
        SnmpVarBind vb = pdu.getFirstVarBind();
        if(vb != null)
        {
            printAuthFailReason(vb.getName());
        }
    }
    
    public static void printAuthFailReason(SnmpOID oid)
    {
        if(oid.equals(AgentUsmStats.USM_STATS_UNKNOWN_USER_NAMES))
        {
            System.out.println( "Reason: Unknown user name");
        }
        else if(oid.equals(AgentUsmStats.USM_STATS_DECRYPTION_ERRORS))
        {
            System.out.println( "USM decryption error");               
        }
        else if(oid.equals(AgentUsmStats.USM_STATS_WRONG_DIGESTS))
        {
            System.out.println( "Wrong digest");               
        }
        else if(oid.equals(AgentUsmStats.USM_STATS_UNKNOWN_ENGINE_IDS))
        {
            System.out.println( "Unkown engine ID");               
        }
        else if(oid.equals(AgentUsmStats.USM_STATS_NOT_IN_TIMEWINDOWS))
        {
            System.out.println( "Not in time windows");               
        }
        else if(oid.equals(AgentUsmStats.USM_STATS_UNSUPPORTED_SEC_LEVELS))
        {
            System.out.println( "Unsupported security levels");               
        }
    }

    /**
     * Prints variable binding array to standard out
     */
    protected void print(SnmpVarBind[] varbinds) {
        print(varbinds, System.out);
    }

    /**
     * Prints variable binding array to PrintStream p
     */
    protected void print(SnmpVarBind[] varbinds, PrintStream p) {
        for (int i = 0; i < varbinds.length ; i++) {
            print(varbinds[i], p);
        }
    }
    
    /**
     * Prints variable binding to standard out
     */
    protected void print(SnmpVarBind var)
    {
        print(var, System.out);
    }
    
    /**
     * Prints variable binding to PrintStream p
     */
    protected void print(SnmpVarBind var, PrintStream p)
    {
        if(var == null) {
            return;
        }
        SnmpDataType  n = var.getName();
        SnmpDataType  v = var.getValue();
        
        if(n == null) 
        {
            p.println( "SnmpVarBind 's name is null" );
        }
        String oid = n.toString();
        String value = v.toString();

        // **** Optional, if MIB OID name translation is desired.
        if(MibUtil.isMibFileLoaded())
        {
            //Use MibUtil.translate to translate oid and its value to string
            //format
            NameValue nv = MibUtil.translate(oid, value, true);
            if(nv != null) {
                oid = nv.getName();
                value = nv.getValue();
            }
        }

        if( v.getType() == SnmpDataType.TIMETICKS )
        {//if it's time ticks, translate it to friendly readable format
            value = ((SnmpTimeTicks) v).getTimeString();
        }
        else if( v.getType() == SnmpDataType.END_OF_MIB_VIEW )
        {
            p.println( "End of MIB reached.");
            return;
        }
        else if( v.getType() == SnmpDataType.NO_SUCH_OBJECT )
        {
            p.println( "No such object.");
            return;
        }
        else if( v.getType() == SnmpDataType.NO_SUCH_INSTANCE )
        { 
            p.println( "No such Instance.");
            return;
        }
        p.println( oid + "\r\nValue (" + v.getTypeString() +
                "): "+ value + "\r\n");
    }

    /**
     * Creates a new instance of ParseArguments
     */
    protected ParseArguments newParseArgumentsInstance(String[] args)
    {
        return new ParseArguments(args, "?h", "vxcpk");
    }
    
    
   /**
     * Parses command options
     * @param args the command arguments
     * supplied.
     */
    protected void parseOptions(String[] args)
    {
        _parseArgs = newParseArgumentsInstance(args);
        if(_parseArgs.isSwitchPresent('?') ||_parseArgs.isSwitchPresent('h') ) {
        	//for help, print usage.
            printUsage();
            System.exit(0);
        }
        
        _version = Integer.parseInt(_parseArgs.getOptionValue('v', "1")) ;//default is SNMPv1
        if( _version < 3) _version --;

        SnmpSession.loadMib2(); //preload MIB-II (RFC1213) module

        
        _community = _parseArgs.getOptionValue('c', "si2019");
        _port = Integer.parseInt(_parseArgs.getOptionValue('p', "161"));
        String transport = _parseArgs.getOptionValue('k', "UDP");
        _transportLayer = "UDP".equalsIgnoreCase(transport) ? Session.UDP : Session.TCP;
        parseArgs();
    }

    /**
     * Parses non-option arguments
     */
    protected void parseArgs()
    {
        String [] as = _parseArgs.getArguments();
        if(as.length > 0)
            _host = as[0];
        else
        	_host = DEFAULT_HOST;

     
    }
    
    /**
     * Prints values of all options
     */
    protected void printOptions()
    {
        System.out.println( "Options:");
        System.out.println( "_____________________________________");
        System.out.println( "host =\t\t\t" + _host);
        System.out.println( "port =\t\t\t" + _port);
//        System.out.println( "isSnmpV3 =\t\t" + _isSnmpV3);
//        System.out.println( "authProtocol =\t\t" + _authProtocol);
//        System.out.println( "authPassword =\t\t" + _authPassword);
//        System.out.println( "privProtocol =\t\t" + _privProtocol);
//        System.out.println( "privPassword =\t\t" + _privPassword);
        System.out.println( "community =\t\t" + _community);
//        System.out.println( "user =\t\t\t" + _user);
        printMoreOptions();
        System.out.println( "_____________________________________");
    }

    /**
     * Prints values of other options if any. Can be implemented by subclasses
     */
    protected void printMoreOptions()
    {
    }

    

    //
    //  ------------ utility methods
    //

    /**
     * Translates to SnmpDataType object. Used in snmptrap and snmpset classes
     * @param type  one of i, u, t, a, o, s, c
     * i: INTEGER, u: unsigned INTEGER, t: TIMETICKS,
     * a: IPADDRESS, o: OBJID, s: STRING, c: counter32, g: gauge, x: hex data (in "0x1B 0xAC ..." format)
     * @param value  value 
     */
    public static SnmpDataType translate(String type, String value)
    {
        if(type.equals("i"))
        {
            return new SnmpInt(Integer.parseInt(value));
        }
        else if(type.equals("u"))
        {
            return new SnmpUInt(Long.parseLong(value));
        }
        else if(type.equals("t"))
        {
            return new SnmpTimeTicks(Long.parseLong(value));
        }
        else if(type.equals("a"))
        {
            return new SnmpIpAddress(value);
        }
        else if(type.equals("o"))
        {
            return new SnmpOID(value);
        }
        else if(type.equals("s"))
        {
            return new SnmpOctetString(value);
        }
        else if(type.equals("x"))
        {
            return new SnmpOctetString(SnmpOctetString.getBytes(value));
        }
        else if(type.equals("c"))
        {
            return new SnmpCounter32(value);
        }
        else if(type.equals("g"))
        {
            return new SnmpGauge32(value);
        }
        else
        {
            throw new RuntimeException("Unknown data type");
        }
    }

    /**
     * Helper method, returns byte array of passed hex or regular string
     */
    public static byte [] getHexString(String text)
    {
        if(text.startsWith("0x") || text.startsWith("0X"))
        {//expecting something like "0X01ACDF11"
            int len = text.length() - 2;
            if(len % 2 != 0) 
            {
                throw new IllegalArgumentException("Illegal hex string");
            }
            byte [] ret = new byte[len/2];
            int j = 0;
            for (int i = 0; i < len ; ) 
            {
                String s = text.substring(i + 2, i + 4);
                int value = Integer.parseInt(s, 16);
                ret[j++] = (byte) value;
                i += 2;
            }

            return ret;
        }
        else
        {//regular string
            return text.getBytes();
        }
    }

}
// end of class snmp