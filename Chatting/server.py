#!/usr/bin/env python
# -*- coding: utf-8 -*-

from xmlrpc.server import SimpleXMLRPCServer
from xmlrpc.server import SimpleXMLRPCRequestHandler



class RequestHandler(SimpleXMLRPCRequestHandler):
    rpc_paths = ('/RPC2',)


# Create server
with SimpleXMLRPCServer(('localhost', 8000), requestHandler=RequestHandler) as server:
    class MyFuncs:
        @staticmethod
        def say(sth):
            print("TA说: ", sth)

    server.register_instance(MyFuncs())
    # Run the server's main loop
    server.serve_forever()
