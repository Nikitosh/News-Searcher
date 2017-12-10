import socket
import json
from flask import Flask, render_template, request, jsonify

app = Flask(__name__)
HOST = "localhost"
PORT = 8080

socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
socket.connect((HOST, PORT))

def transform_bold(text, is_bold):
	print(text)
	print(is_bold)
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
	BUFFER_SIZE = 10000
	results = json.loads(socket.recv(BUFFER_SIZE).decode('utf-8'))
	for result in results:
		result['content'] = transform_bold(result['content'], result['isBold'])
	#print(results)
	return render_template('index.html', results=results)

if __name__ == '__main__':
	app.run()