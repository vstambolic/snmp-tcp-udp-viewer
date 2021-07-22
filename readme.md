## SNMP TCP/UDP Viewer

Java Swing application for observing TCP/UDP sockets of a network simulated in GNS3 using SNMP protocol in real time.

## Requierements

- [Java SE Development Kit 8 or higher](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html)
- [GNS3](https://www.gns3.com/)
- [iReasoning SNMP Java API](https://ireasoning.com/snmpapi.shtml)


## Network configuration

Topology of a network simulated in GNS3 is given in the following screenshot:

<p  align="center">
<img src="https://user-images.githubusercontent.com/61201104/126668712-ad82d493-3b00-4cce-86c7-c5493e44f9b9.JPG" 
 width="80%">
</p>


Every router in the network has SNMP and BGP configured. SNMP versions 1 and 2c are supported. Loopback addresses of the routers (where SNMP is configured) are

- 192.168.10.1 for R1;
- 192.168.20.1 for R2;
- 192.168.30.1 for R3;

but the application communicates with R1 only, which has http server configured for testing purposes.

Before using the application, the ip route table of the host machine (from the picture) must be modified so it contains neccessary routes from the network. That can be done manually, or by executing <b>setup.sh</b> script.

## Example of running application

<p  align="center">
<img src="https://user-images.githubusercontent.com/61201104/126667532-62d7be8a-6520-4bdf-9b3b-b6543b5bab26.png" 
 width="80%">
</p>
