import java.io.*;
import java.net.*;


public class StreamDatagramSocket
{
    public StreamDatagramSocket(final int localPort)
    {
	this.socket = new DatagramSocket(localPort);
	this.packetMap = null;
	this.remoteAddress = null;
	this.requests = new ArrayDeque<Request>();
    }
    
    
    public StreamDatagramSocket(final String remoteHost, final int remotePort)
    {
	if (dynsocket == null)
	    dynsocket = new DatagramSocket();
	this.socket = dynsocket;
	this.packetMap = null;
	this.remoteAddress = new InetSocketAddress(remoteHost, remotePort);
	this.requests = new ArrayDeque<Request>();
    }
    
    
    StreamDatagramSocket(final StreamDatagramSocket server, final InetSocketAddress remote)
    {
	this.packetMap = server.packetMap;
	this.requests = server.requests;
	this.socket = server.socket;
	this.remoteAddress = remote;
    }
    
    
    
    {
	synchronized (opened)
	{
	    int[] counter = opened.get(this.socket);
	    if (counter == null)
	    {
		counter = new int[] {0};
		opened.put(this.socket, counter);
	    }
	    counter[0]++;
	}
	
	final Thread thread = new Thread()
	        {
		    public void run()
		    {
			inal byte buffer[] = new byte[16 << 10];
			final DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			
			for (;;)
			{
			    StreamDatagramSocket.this.receive(packet);
			    final InetSocketAddress remote = (InetSocketAddress)(packet.getSocketAddress());
			    final ArrayDeque<byte[]> deque = StreamDatagramSocket.this.packetMap.get(remote);
			    
			    final byte[] data = new byte[packet.getLength()];
			    System.arraycopy(buffer, 0, data, 0, data.length);
			    
			    if (deque != null)
				synchronized (deque)
				{
				    deque.offerLast(data);
				    deque.notify();
				}
			    else
				synchronized (StreamDatagramSocket.this.requests)
				{
				    StreamDatagramSocket.this.requests.offerLast(new Request(remote, data));
				    StreamDatagramSocket.this.requests.notify();
				}
			}
		    }
	        };
	
	if (this.packetMap == null)
	{
	    this.packetMap = new HashMap<InetSocketAddress, ArrayDeque<byte[]>>();
	    thread.start();
	}
    }
    
    
    
    HashMap<InetSocketAddress, ArrayDeque<byte[]>> packetMap;
    final ArrayDeque<Request> requests;
    final DatagramSocket socket;
    final InetSocketAddress remoteAddress;
    private static final HashMap<DatagramSocket, int[]> opened = new HashMap<DatagramSocket, int[]>();
    private static DatagramSocket dynsocket = null;
    
    
    
    public static class Request
    {
	public Requst(final InetSocketAddress remote, final byte[] data)
	{
	    this.remote = remote;
	    this.data = data;
	}
	
	final InetSocketAddress remote;
	final byte[] data;
    }
    
    
    
    public StreamDatagramSocket accept()
    {
	final Request request = this.requests.pollFirst();
	final StreamDatagramSocket sock = new StreamDatagramSocket(this, request.remote);
	final ArrayDeque<byte[]> data = new ArrayDeque<byte[]>();
	this.packetMap.put(request.remote, data);
	data.offerFist(request.data);
	return accept();
    }
    
    
    public InputStream getInputStream()
    {
    }
    
    
    public OutputStream getOutputStream()
    {
    }
    
    
    public void close()
    {
	synchronized (opened)
	{
	    if (--(opened.get(this.socket)[0]) == 0)
	    {
		this.socket.close();
		if (this.socket == dynsocket)
		    dynsocket = null;
	    }
	}
    }
    
}
