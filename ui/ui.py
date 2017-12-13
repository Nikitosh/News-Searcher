import socket
import json
import sys
from flask import Flask, render_template, request

app = Flask(__name__)

if len(sys.argv) < 2:
	print('There should be server ip address argument')
	exit(0)
HOST = sys.argv[1]
PORT = 8080

socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
socket.connect((HOST, PORT))

def transform_bold(text, is_bold):
	result = ''
	for i in range(len(text)):
		if is_bold[i]:
			result += '<b>' + text[i] + '</b>'
		else:
			result += text[i]
	return result

@app.route('/', methods=['GET', 'POST'])
def index():
    return render_template('index.html')

@app.route('/search', methods=['GET'])
def get_counts():
	query = request.args['query']
	socket.sendall(bytes(query + '\n', 'utf-8'))
	BUFFER_SIZE = 1024
	data = bytearray()
	while True:
		data += socket.recv(BUFFER_SIZE)
		if data.find(bytearray([10])) != -1:
			break
	results = json.loads(data.decode('utf-8'))
	for result in results:
		result['content'] = transform_bold(result['content'], result['isBold'])
	return render_template('index.html', results=results)

if __name__ == '__main__':
	app.run()     		