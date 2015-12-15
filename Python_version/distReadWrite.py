'''
TODO
* Synchronisation of join
  - Way to deal with inconsistency when two separate clusters try to join the the network simultaneously
    or to put it into perspectve : what to do about the request to join from a cluster when a network update is in progress.
* Synchronisation of join
'''

import xmlrpclib, sys, argparse, threading, SocketServer, menu
from SimpleXMLRPCServer import SimpleXMLRPCServer

# Some constants
IP = 0
PORT = 1                      

# Network address of the current node
my_ip = "localhost"
my_port =  2000

# Store ip_addr and port of each neighbor
my_neighbor = []                      

# Mix-in for threaded xmlrpc server
class ThreadedXMLRPCServer(SocketServer.ThreadingMixIn, SimpleXMLRPCServer):pass

class MethodLibrary:
	# Updates the neighbor with the list received from node of another cluster
	def update_neighbor(self, node_ip, node_port, neighbor_list):
		global my_neighbor, my_ip, my_port
		print "(", my_ip , "," , my_port, ")== Update ==>(", node_ip , "," , node_port, ")"
		if (node_ip, node_port) not in my_neighbor:
			return False
		for n in neighbor_list:
			my_neighbor.append(n)

	def join(self, node_ip, node_port, neighbor_list):
		global my_neighbor, my_ip, my_port
		print "(",my_ip,",",my_port,")== Join ==>(",node_ip,",",node_port,")"
		if (node_ip, node_port) in my_neighbor:
			return False
		my_old_neighbor = my_neighbor
		neighbor_list.append((node_ip, node_port))
		for my_n in my_neighbor:
			# Proxy is the server in contact and client in the local context
			proxy_addr = "http://"+node_ip+":"+str(node_port)+"/"
			proxy  = xmlrpclib.ServerProxy(proxy_addr, allow_none=True)
			proxy.update_neighbor(my_ip, my_port, neighbor_list)
		my_neighbor.append((node_ip, node_port))
		print my_neighbor
		return ((my_ip, my_port), my_old_neighbor)

	def sign_out(self, node_ip, node_port):
		global my_neighbor
		my_neighbor.remove((node_ip, node_port))

def run_client():
	global my_neighbor, my_ip, my_port
	proxy = xmlrpclib.ServerProxy("http://localhost:2000/", allow_none=True)
	response = proxy.join(my_ip, my_port, my_neighbor)
	if response != False:
		(peer_address, neighbor_list) = response
		my_neighbor.append(peer_address)
	print my_neighbor

def run_server():
	global my_neighbor, my_ip, my_port
	server = ThreadedXMLRPCServer(("localhost", my_port), allow_none=True)
	server.register_instance(MethodLibrary())
	server.serve_forever()
	# Make an interface for join and signup
	#if my_port != 2000:
	#	client = threading.Thread(target=run_client)
	#	client.setDaemon(True)
	#	client.start()
	#	client.join()


def updateFunction():
	print "Updated"

def join():
	peer_ip = input('Ip address of the node to join: ')
	peer_port = input('Port of the node: ')
	peer_address = 'http://' + peer_ip + ':' + str(peer_port) + '/' 
	global my_neighbor, my_ip, my_port
	peer = xmlrpclib.ServerProxy(peer_address, allow_none=True)
	response = peer.join(my_ip, my_port, my_neighbor)
	if response != False:
		print "Join Request Successful"
		(peer_address, neighbor_list) = response
		my_neighbor.append(peer_address)
	else:
		print "Join Request Fail"

def printPeerAddress():
	global my_neighbor
	print "Peer Address"
	print my_neighbor
        lol = input()

def sign_off():
	global my_neighbor, my_ip, my_port
	for (peer_ip, peer_port) in my_neighbor:
		peer_addr = "http://" + peer_ip + ":" + str(peer_port) + "/"
		peer  = xmlrpclib.ServerProxy(peer_addr, allow_none=True)
		peer.sign_out(my_ip, my_port)

def startDistReadWrite():
	print "asdfasf"
	lol = input()

def clientMenu():
	clientMenu = menu.Menu("Distributed Read/Write")
	options = [{"name":"Start Distributed Read/Write","function":startDistReadWrite},
			{"name":"Join","function":join},
			{"name":"Sign Off","function":sign_off},
			{"name":"Print Peer Address","function":printPeerAddress}]
	clientMenu.addOptions(options)
	clientMenu.open()

def main():
	global my_port
	serverThread = threading.Thread(target=run_server)
	serverThread.setDaemon(True)
	serverThread.start()
	clientMenu()
	serverThread.join()

def parse_args():
	global my_port
	parser = argparse.ArgumentParser(prog='distReadWrite', description='This program performs distributed read write in a logical network.')
	parser.add_argument('port', help='port number for the program to listen to', nargs=1, type=int)
	my_port = int(parser.parse_args().port[0])
	
if __name__ == "__main__":
	parse_args()	
	main()
