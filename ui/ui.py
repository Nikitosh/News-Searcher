import socket
import json
from flask import Flask, render_template, request, jsonify

app = Flask(__name__)
HOST = "localhost"
PORT = 8080

socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
socket.connect((HOST, PORT))

@app.route('/', methods=['GET', 'POST'])
def index():
    return render_template('index.html')

@app.route('/search', methods=['GET'])
def get_counts():
	query = request.args['query']
	socket.sendall(bytes(query + '\n', 'utf-8'))
	BUFFER_SIZE = 10000
	results = json.loads(socket.recv(BUFFER_SIZE).decode('utf-8'))
	return render_template('index.html', results=results)

if __name__ == '__main__':
	app.run()