import xmlrpclib, sys, argparse, threading, SocketServer, menu
from SimpleXMLRPCServer import SimpleXMLRPCServer                   

# Network address of the current node
my_ip   = 'localhost'
global my_port, my_uuid

# Stores (uuid, ip_addr, port) of each connected node
my_neighbor = []                      

# Mix-in for threaded xmlrpc server
class ThreadedXMLRPCServer(SocketServer.ThreadingMixIn, SimpleXMLRPCServer):pass

class MethodLibrary:
	# Updates the neighbor with the list received from node of another cluster
	def update_neighbor(self, friend_ip, friend_port, new_node_ip, new_node_port, new_node_neighbor_list):
		print "Update request from : (", friend_ip, "," , friend_port, ")"
		global my_neighbor, my_ip, my_port

		# Check if trusted node is sending the request
		if (friend_ip, friend_port) not in my_neighbor:
			print "Update request received from untrusted party"
			return False
		
		# Add new node and its neighbor to the neighbor address
		print "New node joining the group : (", new_node_ip , "," , new_node_port, ")"
		if (new_node_ip, new_node_port) in my_neighbor:
			print "Node already present in our group"
			return False

		my_neighbor.append((new_node_ip, new_node_port))	
		for (n_ip, n_port) in new_node_neighbor_list:
			my_neighbor.append((n_ip, n_port))
		
		print "New node successfully added to the group"
		#print 'Press any button to return to main screen'
		#wait = raw_input()

	def join(self, peer_node_ip, peer_node_port, neighbor_list):
		global my_neighbor, my_ip, my_port
		print "Join request received from, ", "(", peer_node_ip,",", peer_node_port,")"
		
		# Check if node is already in connection
		if (peer_node_ip, peer_node_port) in my_neighbor:
			print "Redundant request : Node already connected"
			return False

		my_old_neighbor = my_neighbor[:]
		for (neighbor_ip, neighbor_port) in my_neighbor:
			# Proxy is the server in contact and client in the local context
			proxy_addr = "http://"+neighbor_ip+":"+str(neighbor_port)+"/"
			proxy  = xmlrpclib.ServerProxy(proxy_addr, allow_none=True)
			proxy.update_neighbor(my_ip, my_port, peer_node_ip, peer_node_port ,neighbor_list)
		my_neighbor.append((peer_node_ip, peer_node_port))
		
		print "Join request Approved. New node added to the group"
		return ((my_ip, my_port), my_old_neighbor)

	def sign_out(self, node_ip, node_port):
		global my_neighbor
		my_neighbor.remove((node_ip, node_port))
		print "(", node_ip, ",", node_port, ")", "signed out of the group"

def run_server():
	global my_neighbor, my_ip, my_port
	server = ThreadedXMLRPCServer(("localhost", my_port), allow_none=True)
	server.register_instance(MethodLibrary())
	server.serve_forever()

def updateFunction():
	print "Updated"

def join():
	global my_neighbor, my_ip, my_port
	peer_ip = raw_input('Ip address of the node to join: ')
	peer_port = raw_input('Port of the node: ')
	
	# Cannot connect to self		
	if (peer_ip, peer_port) == (my_ip, str(my_port)):
		return

	peer_address = 'http://' + peer_ip + ':' + str(peer_port) + '/' 
	peer = xmlrpclib.ServerProxy(peer_address, allow_none=True)
	response = peer.join(my_ip, my_port, my_neighbor)
	if response == False:
		print "Join Request Fail"
	else:
		print "Join Request Successful"
		((peer_ip, peer_port), peer_neighbor_list) = response

		# Update list of neighbours in the cluser
		for (neighbor_ip, neighbor_port) in my_neighbor:
			proxy_addr = "http://"+neighbor_ip+":"+str(neighbor_port)+"/"
			proxy  = xmlrpclib.ServerProxy(proxy_addr, allow_none=True)
			proxy.update_neighbor(my_ip, my_port, peer_ip, peer_port , peer_neighbor_list)
		
		# Update own neighbor list
		for (n_ip, n_port) in peer_neighbor_list:
			my_neighbor.append((n_ip, n_port))
		my_neighbor.append((peer_ip, peer_port))
		
	print 'Press any button to return to main screen'
	wait = raw_input()

def printPeerAddress():
	global my_neighbor
	print "Peer Address"
	print my_neighbor
	print 'Press any button to reuturn to main screen'
	wait = raw_input()

def sign_off():
	global my_neighbor, my_ip, my_port
	for (peer_ip, peer_port) in my_neighbor:
		peer_addr = "http://" + peer_ip + ":" + str(peer_port) + "/"
		peer  = xmlrpclib.ServerProxy(peer_addr, allow_none=True)
		peer.sign_out(my_ip, my_port)
	my_neighbor = []

def startDistReadWrite():
	print "asdfasf"
	
def clientMenu():
	menuInterface = menu.Menu("Distributed Read/Write")
	options = [{"name":"Start Distributed Read/Write","function":startDistReadWrite},
		{"name":"Join","function":join},
		{"name":"Sign Off","function":sign_off},
		{"name":"Print Peer Address","function":printPeerAddress},
		{"name":"Terminate program","function":sys.exit},
		]
	menuInterface.addOptions(options)
	menuInterface.open()
	
if __name__ == "__main__":
	# Get port number as command line input
	parser = argparse.ArgumentParser(prog='distReadWrite', 
				description='This program performs distributed read write in a logical network.')
	parser.add_argument('port', help='port number for the program to listen to', nargs=1, type=int)
	my_port = int(parser.parse_args().port[0])

	# Start server thread
	serverThread = threading.Thread(target=run_server)
	serverThread.setDaemon(True)
	serverThread.start()
	
	# Start client interface
	clientMenu()
	serverThread.join()
	














