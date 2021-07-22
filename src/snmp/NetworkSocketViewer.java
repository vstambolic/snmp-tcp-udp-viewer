package snmp;

import com.ireasoning.protocol.snmp.*;

import gui.*;

import java.awt.GridLayout;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.table.TableModel;

public class NetworkSocketViewer extends SNMP {

	private static int REFRESH_INTERVAL = 5;

	private SnmpTarget target;
	private SnmpSession session;
	private HashMap<String, SnmpTableModel> tables = new HashMap<>();
	
	// Gui ---------------------------------
	private JFrame jf = new JFrame();
	private JPanel jp = new JPanel(new GridLayout(2,1));;
	{
		jf.setSize(790, 450);
		jf.setResizable(false);
		jf.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				tables.get("tcpConnTable").stopPolling();
				tables.get("udpTable").stopPolling();
				session.close();
				System.exit(0);
			}
		});		
		jf.add(jp);
	}
			
			
	public static void main(String[] args) {
		NetworkSocketViewer nsm = new NetworkSocketViewer();
		nsm.parseOptions(args);

		nsm.startSession();
		
		nsm.getTable("tcpConnTable");
		nsm.getTable("udpTable");
		
		nsm.jf.setVisible(true);


	}

	private void startSession() {
		target = new SnmpTarget(_host, _port, _community, _community, _version);
		try {
			session = new SnmpSession(target, _transportLayer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Loading mib-2 already handled in super class (SNMP.java)
	}

	private void getTable(String tableName) {
		try {

			SnmpTableModel table;
			tables.put(tableName,table = session.snmpGetTable(tableName));

			// Check for problems
			if (table == null) {
				System.err.println("Table not found in loaded MIBs");
				return;
			}
			SnmpOID authFailureOID = table.getAuthFailureOID();
			if (authFailureOID != null) {
				System.out.println("Authentication failed. Reason:");
				SNMP.printAuthFailReason(authFailureOID);
				return;
			}

			// Print to standard out
//			for (int i = 0; i < table.getRowCount(); i++) {
//				super.print(table.getRow(i));
//			}

			table.setTranslateValue(true);        // to use values defined in MIB
			table.startPolling(REFRESH_INTERVAL); // poll table every 5 seconds
			this.showTable(table);			      // show the result in Swing's JTable

					
//			Thread.sleep(30 * 1000);
//			table.refreshNow();
			
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}

	/** Displays table in a JFrame */
	private void showTable(TableModel model) {
		DarkJTable jt = new DarkJTable(model);
		DarkJScrollPane pane = new DarkJScrollPane(jt);
		jp.add(pane);
	}

}
