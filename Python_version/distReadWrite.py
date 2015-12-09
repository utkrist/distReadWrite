import xmlrpclib, sys, argparse, threading
from SimpleXMLRPCServer import SimpleXMLRPCServer

# Some constants
IP = 0
PORT = 1

# Network address of the current node
my_ip = "localhost"
my_port =  2000

# Store ip_addr and port of each neighbor
my_neighbor = []

# Updates the neighbor with the list received from node of 
# another cluster
def update_neighbor(node_ip, node_port, neighbor_list):
	global my_neighbor, my_ip, my_port
	print "(", my_ip , "," , my_port, ")== Update ==>(", node_ip , "," , node_port, ")"
	if (node_ip, node_port) not in my_neighbor:
		return False
	for n in neighbor_list:
		my_neighbor.append(n)

def join(node_ip, node_port, neighbor_list):
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

def sign_out(node_ip, node_port):
	global my_neighbor
	my_neighbor.remove((node_ip, node_port))

def usage():
	print "Usage: distReadWrite --port (or-p) <PORT>"
	sys.exit(0)

def parse_args():
	global my_port
	parser = argparse.ArgumentParser(prog='distReadWrite', description='This program performs distributed read write in a logical network.')
	parser.add_argument('port', help='port number for the program to listen to', nargs=1, type=int)
	my_port = int(parser.parse_args().port[0])

def run_server():
	global my_neighbor, my_ip, my_port
	server = SimpleXMLRPCServer(("localhost", my_port), allow_none=True)
	server.register_function(join, "join")
	server.register_function(update_neighbor, "update_neighbor")
	server.register_function(sign_out, "sign_out")
	server.serve_forever()

def run_client():
	global my_neighbor, my_ip, my_port
	proxy = xmlrpclib.ServerProxy("http://localhost:2000/", allow_none=True)
	response = proxy.join(my_ip, my_port, my_neighbor)
	if response != False:
		(peer_address, neighbor_list) = response
		my_neighbor.append(peer_address)
	print my_neighbor

def main():
	global my_port
	server = threading.Thread(target=run_server)
	server.setDaemon(True)
	server.start()	
	if my_port != 2000:
		client = threading.Thread(target=run_client)
		client.setDaemon(True)
		client.start()
		client.join()
	server.join()
	
if __name__ == "__main__":
	parse_args()
	main()
