from server import *
t_server = threading.Thread(target=serverThread)
t_server.start()
t_tick_loop = threading.Thread(target=tickLoopThread)
t_tick_loop.start()
