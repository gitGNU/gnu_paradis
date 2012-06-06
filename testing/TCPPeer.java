import java.io.*;
import java.net.*;


public class TCPPeer
{
    public static final int PORT = 59692;
    
    
    public static void main(final String... args) throws IOException
    {
	final char name = args[0].charAt(0);
	final boolean serverauth = args[1].charAt(0) == 's';
	final String pubip = args[2];
	final String[] remotes = new String[args.length - 3];
	
	for (int i = 3, n = args.length; i < n; i++)
	    remotes[i - 3] = args[i];
	
	
	System.out.println("I am " + name +
			   ", and " + (serverauth ? "may" : "may not") +
			   " be server on\n [" + pubip + "]:" + PORT);
	
	start(name, serverauth, pubip, remotes);
    }
    
    
    /**
     * @param  name        The name of the peer
     * @param  serverauth  Whether the peer may be the network's server
     * @param  pubip       The local public IP address
     * @param  remotes     The remote public IP addresses
     */
    public static void start(final char name, final boolean serverauth, final String pubip, final String... remotes) throws IOException
    {
	final String[] peers = { Character.toString(name) };
	final Object monitor = new Object();
	
	try
	{
	    System.out.println("Trying to connect to local server");
	    connect(new Socket(pubip, PORT), monitor, peers);
	    System.out.println("I am connected to local server");
	}
	catch (final Throwable err)
	{
	    System.out.println("Failed to connect to local server");
	    
	    ServerSocket _server = null;
	
	    if (serverauth)
		try
		{
		    System.out.println("Trying to make local server");
		    _server = new ServerSocket(PORT);
		}
		catch (final BindException ierr) //"Address already in use" | "Permission denied"
		{
		    System.out.println("Failed to make local server");
		    _server = null;
		}
	
	    final ServerSocket server = _server;
	
	    if (server != null)
	    {
		System.out.println("I am a server");
		
		final Thread threadServer = new Thread()
		        {
			    @Override
			    public void run()
			    {
				try
				{
				    for (;;)
					connect(server.accept(), monitor, peers);
				}
				catch (final IOException ierr)
				{
				    System.out.println("Server error");
				}
			    };
		    };
		
		threadServer.start();
	    }
	}
	
	for (final String remote : remotes)
	    try
	    {
		System.out.println("Trying to connect to remote server: " + remote);
		connect(new Socket(remote, PORT), monitor, peers);
		System.out.println("I am connected to remote server: " + remote);
	    }
	    catch (final Throwable ierr)
	    {
		System.out.println("Client error: " + remote);
	    }
	
	System.out.println("I am done");
    }
    
    
    private static void connect(final Socket sock, final Object monitor, final String[] peers) throws IOException
    {
	final InputStream in = sock.getInputStream();
	final OutputStream out = sock.getOutputStream();
	
	final Thread threadIn = new Thread()
	        {
		    @Override
		    public void run()
		    {
			try
			{
			    for (;;)
			    {
				final String oldpeers = peers[0];
				
				String newpeers = new String();
				for (int p; (p = in.read()) != '\n';)
				    newpeers += (char)p;
				
				synchronized (monitor)
				{
				    for (int i = 0, n = newpeers.length(); i < n; i++)
					if (peers[0].indexOf(newpeers.charAt(i)) < 0)
					    peers[0] += newpeers.charAt(i);
				    
				    if (peers[0].equals(oldpeers) == false)
				    {
					System.out.println("I can send to: " + peers[0]);
					
					monitor.notifyAll();
				    }
				}
			    }
			}
			catch (final IOException err)
			{
			    synchronized (TCPPeer.class)
			    {
				System.err.print("\033[31m");
				System.err.println("error: IOException");
				err.printStackTrace(System.err);
				System.err.print("\033[0m");
			    }
			}
		    }
	        };
	
	final Thread threadOut = new Thread()
	        {
		    @Override
		    public void run()
		    {
			try
			{
			    synchronized (monitor)
			    {
				for (;;)
				{
				    for (int i = 0, n = peers[0].length(); i < n; i++)
					out.write(peers[0].charAt(i));
				    out.write('\n');
				    out.flush();
				    
				    monitor.notifyAll();
				    monitor.wait();
				}
			    }
			}
			catch (final InterruptedException err)
			{
			    synchronized (TCPPeer.class)
			    {
				System.err.println("error: InterruptedException");
			    }
			}
			catch (final IOException err)
			{
			    synchronized (TCPPeer.class)
			    {
				System.err.print("\033[33m");
				System.err.println("error: IOException");
				err.printStackTrace(System.err);
				System.err.print("\033[0m");
			    }
			}
		    }
	        };
	
	threadIn.start();
	threadOut.start();
    }
    
}

