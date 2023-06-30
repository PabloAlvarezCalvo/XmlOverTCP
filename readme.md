The goal is to create an application that sends an XML message over TCP sockets of the following format:
```
<?xml version="1.0" encoding="UTF-8"?><Msg Name="OpenStudies" Type="XA"><Param Name="ProcessId">placeholder</Param></Msg>
```
Tha application will request the user an IP address, a port and the value to replace "placeholder" with.

Application requirements
1.	Must run on Windows x64 enviroments.
2.	Must be coded with java 11 LTS.
