import subprocess
import time

# Define the server and client Java files
TCP_SERVER = "TCPServer.java"
UDP_SERVER = "UDPServer.java"
TCP_CLIENT = "TCPClient.java"
UDP_CLIENT = "UDPClient.java"

# Define the message to send
MESSAGE = """O Captain! my Captain! our fearful trip is done,
The ship has weather’d every rack, the prize we sought is won,
The port is near, the bells I hear, the people all exulting,
While follow eyes the steady keel, the vessel grim and daring;
But O heart! heart! heart! O the bleeding drops of red,
Where on the deck my Captain lies, Fallen cold and dead.
O Captain! my Captain! rise up and hear the bells;
Rise up—for you the flag is flung—for you the bugle trills,
For you bouquets and ribbon’d wreaths—for you the shores a-crowding,
For you they call, the swaying mass, their eager faces turning;
Here Captain! dear father! This arm beneath your head!
It is some dream that on the deck, You’ve fallen cold and dead.
My Captain does not answer, his lips are pale and still,
My father does not feel my arm, he has no pulse nor will,
The ship is anchor’d safe and sound, its voyage closed and done,
From fearful trip the victor ship comes in with object won;
Exult O shores, and ring O bells! But I with mournful tread,
Walk the deck my Captain lies, Fallen cold and dead."""

# Define the test cases
LATENCY_TESTS = [
    {"protocol": "TCP", "bytes": 8, "iterations": 30},
    {"protocol": "TCP", "bytes": 64, "iterations": 30},
    {"protocol": "TCP", "bytes": 256, "iterations": 30},
    {"protocol": "TCP", "bytes": 512, "iterations": 30},
    {"protocol": "UDP", "bytes": 8, "iterations": 30},
    {"protocol": "UDP", "bytes": 64, "iterations": 30},
    {"protocol": "UDP", "bytes": 256, "iterations": 30},
    {"protocol": "UDP", "bytes": 512, "iterations": 30},
]

THROUGHPUT_TESTS = [
    {"protocol": "TCP", "message_size": 1024, "num_messages": 1024, "iterations": 30},
    {"protocol": "TCP", "message_size": 512, "num_messages": 2048, "iterations": 30},
    {"protocol": "TCP", "message_size": 256, "num_messages": 4096, "iterations": 30},
    {"protocol": "UDP", "message_size": 1024, "num_messages": 1024, "iterations": 30},
    {"protocol": "UDP", "message_size": 512, "num_messages": 2048, "iterations": 30},
    {"protocol": "UDP", "message_size": 256, "num_messages": 4096, "iterations": 30},
]

def compile_java_files():
    """Compile the Java server and client files."""
    print("Compiling Java files...")
    subprocess.run(["javac", TCP_SERVER])
    subprocess.run(["javac", UDP_SERVER])
    subprocess.run(["javac", TCP_CLIENT])
    subprocess.run(["javac", UDP_CLIENT])
    print("Compilation complete.\n")

def run_latency_test(protocol, bytes, iterations):
    """Run a latency test for the given protocol, bytes, and iterations."""
    server_class = "TCPServer" if protocol == "TCP" else "UDPServer"
    client_class = "TCPClient" if protocol == "TCP" else "UDPClient"

    # Start the server
    server_process = subprocess.Popen(["java", server_class])

    # Wait for the server to start
    time.sleep(2)

    # Run the client
    client_args = [
        "java", client_class,
        "True",  # Latency test
        MESSAGE,
        str(bytes),
        str(iterations),
        "True",  # Print results
    ]
    subprocess.run(client_args)

    # Stop the server
    server_process.terminate()
    server_process.wait()

def run_throughput_test(protocol, message_size, num_messages, iterations):
    """Run a throughput test for the given protocol, message size, and number of messages."""
    server_class = "TCPServer" if protocol == "TCP" else "UDPServer"
    client_class = "TCPClient" if protocol == "TCP" else "UDPClient"

    # Start the server
    server_process = subprocess.Popen(["java", server_class])

    # Wait for the server to start
    time.sleep(2)

    # Run the client
    client_args = [
        "java", client_class,
        "False",  # Throughput test
        str(message_size),
        str(num_messages),
        str(iterations),
        "True",  # Print results
    ]
    subprocess.run(client_args)

    # Stop the server
    server_process.terminate()
    server_process.wait()

def main():
    # Compile Java files
    compile_java_files()

    # Run latency tests
    print("Running Latency Tests...")
    for test in LATENCY_TESTS:
        print(f"Running {test['protocol']} Latency Test with {test['bytes']} bytes for {test['iterations']} iterations...")
        run_latency_test(test["protocol"], test["bytes"], test["iterations"])
        print("Test completed.\n")

    # Run throughput tests
    print("Running Throughput Tests...")
    for test in THROUGHPUT_TESTS:
        print(f"Running {test['protocol']} Throughput Test with {test['message_size']} byte messages and {test['num_messages']} messages for {test['iterations']} iterations...")
        run_throughput_test(test["protocol"], test["message_size"], test["num_messages"], test["iterations"])
        print("Test completed.\n")

if __name__ == "__main__":
    main()