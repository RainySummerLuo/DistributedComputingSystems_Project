#!/usr/bin/env python
# -*- coding: utf-8 -*-

import xmlrpc.client
s = xmlrpc.client.ServerProxy('http://localhost:8000')

while 1:
    sth = input("我说: ")
    reply = s.say(sth)
    print("TA说: ", reply)
