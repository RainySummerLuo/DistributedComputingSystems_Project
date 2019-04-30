from xmlrpc.server import SimpleXMLRPCServer
from xmlrpc.server import SimpleXMLRPCRequestHandler
# Restrict to a particular path.


class RequestHandler(SimpleXMLRPCRequestHandler):
    rpc_paths = ('/RPC2',)


# Create server
with SimpleXMLRPCServer(('localhost', 8000), requestHandler=RequestHandler) as server:
    server.register_introspection_functions()


    class MyFuncs:
        def say(self, sth):
            print("TA说: ", sth)
            ret = input("我说: ")
            return ret

    server.register_instance(MyFuncs())
    # Run the server's main loop
    server.serve_forever()
