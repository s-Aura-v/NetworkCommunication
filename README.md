# Network Communication
## Abstract
Measure the latency and throughput of the following TCP and/or UDP based protocols (as noted below) across at least three pairs of machines using at least two different networks. 

## Background
1. Measure round-trip latency (RTTs) and how it varies with message size in TCP, by sending and receiving (echoing and validating) messages of size 8, 64, 256, and 512 bytes.
2. Measure throughput (bits per second) and how it varies with message size in TCP, by sending 1MByte of data (with a 8-byte acknowledgment in the reverse direction) using different numbers of messages: 1024 1024Byte messages, vs 2048 512Byte messages, vs 4096 X 256Byte messages. Use known message contents (for example number sequences) so they can be validated.
3. The same as (1), except using UDP.
4. The same as (2), using UDP.

## Tools/Skills Used
1. Server Sockets
2. Datagrams

## Program Results
[View Program Results and Graphs](https://htmlpreview.github.io/?https://github.com/s-Aura-v/NetworkCommunication/blob/main/src/main/resources/static/index.html)

## Run Guide
Setup:
mvn clean install 

1. Run server 
    1. For latency testing, run UDPServer.jar or TCPServer.jar
    2. For throughput testing, run UDP2.jar or TCP2.jar
2. Run client
    1. Enter the inputs correctly for proper testing 

## Problems and Solutions
1. UDP loses packets. Client cannot continue without recieving any feedback.
    1. Add a TIMEOUT to client. If it exceeds a certain unit, skip the current packet and send the next one. 
2. Once packet is lost, then the server still has broken packets.
    1. Add a boolean that indicates when client has skipped an iteration. Use that boolean to remove excess details. 
3. Socket not able to send more than 8 messages to server.
    1. Check how try and catch are formatted. If you properly format them, then it should not have this problem.
