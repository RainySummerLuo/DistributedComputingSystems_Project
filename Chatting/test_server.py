import rpyc

test = []
uname = []
myReferences = set()


class MyService(rpyc.Service):

    def on_connect(self):
        """Think o+ this as a constructor of the class, but with
        a new name so not to 'overload' the parent's init"""
        self.fn = None

    def exposed_serverPrint(self, message):
        global test
        uname.append(message)

        for i in myReferences:
            if i is not self.fn:
                i(message + " joined the room")

    def exposed_serverExit(self, name):
        global test
        myReferences.remove(self.fn)
        uname.remove(name)

    def exposed_serverPrintMessage(self, message):
        global test
        test.append(message)

    def exposed_replyWith(self, number):
        return test[number]

    def exposed_replyLength(self, length):
        return len(test)

    def exposed_setCallback(self, fn):
        self.fn = fn  # Saves the remote function for calling later
        global myReferences
        myReferences.add(fn)


if __name__ == "__main__":
    # lists are pass by reference, so the same 'test'
    # will be available to all threads
    # While not required, think about locking!
    from rpyc.utils.server import ThreadedServer

    t = ThreadedServer(MyService, port=5019)
    t.start()

