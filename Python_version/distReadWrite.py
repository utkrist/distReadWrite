import xmlrpclib, sys, getopt
from SimpleXMLRPCServer import SimpleXMLRPCServer

# Some constants
IP = 0
PORT = 1

my_ip = "localhost"
my_port =  2000

# Store ip_addr and port of each neighbor
my_neighbor = [("self", 2), ("localhost", 2000), ("asf", 44)]

def update_neighbor(node_ip, node_port, neighbor_list):
	if (node_ip, node_port) not in my_neighbor:
		return False
	for n in neighbor_list:
		my_neighbor.append(n)

def join(node_ip, node_port, neighbor_list):
	if (node_ip, node_port) in my_neighbor:
		return False
	my_old_neighbor = my_neighbor
	neighbor_list.append((node_ip, node_port))
	for my_n in my_neighbor:
		client  = xmlrpclib.ServerProxy(my_n[IP], my_n[PORT])
		client.update_neighbor(my_ip, my_port, neighbor_list)
	my_neighbor.append((node_ip, node_port))
	return my_old_neighbor

def sign_out(node_ip, node_port):
	neighbor.remove((node_ip, node_port))

def usage():
	print "Usage: distReadWrite --port (or-p) <PORT>"
	sys.exit(0)

def main(argv):
	try:
		opts,args = getopt.getopt(argv, "p:", ["port="])
	except getopt.GetoptError:
		usage()
	if len(args) == 0:
		usage()
	for opt, arg in opts:
		if opt in ("-p", "--port"):
			my_port = arg
		else:
			usage()
	server = SimpleXMLRPCServer(("localhost", 8080))
	server.register_function(join, "join")
	server.register_function(update_neighbor, "update_neighbor")
	server.register_function(sign_out, "sign_out")
	
if __name__ == "__main__":
	print sys.argv
	main(sys.argv[1:])
