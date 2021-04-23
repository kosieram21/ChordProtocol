# USING 1 GRACE DAY

# Authors
- Mitch Kosieradzki (kosie011)
- Shane Kosieradzki (kosie013)

# Compilation
You can clone and build this project using the following commands:

    git clone https://github.com/kosieram21/ChordProtocol.git
    cd ChordProtocol
    javac -d bin ChordProtocol/*.java

The remaining sections assume that you are making calls from within the `bin` directory created during compilation

# Creating Processes
Bellow are instructions on how to create client/server processes.

## Node
The following command can be used to create a node-process:

    java ChordProtocol.Node <node-id> <m> <port-number> [bootstrap-url]

- The `node-id`'s **must** be unique.
- `m` defines the chord-ring-size, by the following expression `2^m`.
- `port-number` is the port that the RMI registry is listening on.
- Where the `bootstrap-url` is a semi-optional argument. It is not required when creating the initial-node,
but is required for all subsequent nodes.
The subsequent nodes, **must** use the initial-node's url as the `bootstrap-url`.

## DictionaryLoader

The following command can be used to create a DictionaryLoader-process:

    java ChordProtocol.DictionaryLoader <port-number> <node-url> <dictionary-file-path>


- `port-number` is the port that the RMI registry is listening on.
- `node-url` is the URL of the node you are trying to communicate with.
- `dictionary-file-path` is the relative-path to the dictionary file (see `sample-dictionary-file.txt`).

## Client
The following command can be used to create a client-process:

    java ChordProtocol.Client <port-number> <node-url>

- `port-number` is the port that the RMI registry is listening on.
- `node-url` is the URL of the node you are trying to communicate with.

# Logging
A log file is generated for each node-process
They use the following naming scheme:

    ChordNode-<client-id>.txt

# Test Case
We tested the correctness of this algorithms by creating eight unique node-processes, across different hosts.
We first brought the initial node online, omitting the `bootstrap-url` in our invocation.
Each subsequent node-processes we brought online used the url of the initial-node as the `bootstrap-url`.

With any non-zero number of node-processes online, we were able to add elements to the distributed dictionary via the `DictionaryLoader` processes.
Adding subsequent nodes did not destructively interfere with the state of the distributed dictionary.

We were then able to retrieve definitions from the distributed dictionary using the client-processes.

The `DictionaryLoader` and `Client` processes were executed on the same host, but **none** of the `Node` processes ran on this host.

## Linux Port Quirk
The Linux Kernel does not allow users to listen on ports between `[0, 1024]` without administrative privileges.
Thus, we ran our test case on port `4321`

## Lab Machines Used

### Nodes
- URL = `csel-kh4250-01.cselabs.umn.edu`, Node-ID = `0`
- URL = `csel-kh4250-02.cselabs.umn.edu`, Node-ID = `1`
- URL = `csel-kh4250-03.cselabs.umn.edu`, Node-ID = `2`
- URL = `csel-kh4250-04.cselabs.umn.edu`, Node-ID = `3`
- URL = `csel-kh4250-05.cselabs.umn.edu`, Node-ID = `4`
- URL = `csel-kh4250-06.cselabs.umn.edu`, Node-ID = `5`
- URL = `csel-kh4250-07.cselabs.umn.edu`, Node-ID = `6`
- URL = `csel-kh4250-08.cselabs.umn.edu`, Node-ID = `7`

Note that `csel-kh4250-01.cselabs.umn.edu` was the initial-node, i.e. it was used for bootstrapping.

## Client & DictionaryLoader
Both the `Client` and `DictionaryLoader` processes were run on `csel-kh4250-09.cselabs.umn.edu`.

Note that this host is **NOT** one of the `Node` hosts.

# Known Bugs
- Multiple `Nodes` cannot run on the same host.
   - We consider this bug to be minor, since in a real-world situation it is unlikely that multiple `Nodes` would run on the same host, 
     as it defeats the purpose of a distributed system.