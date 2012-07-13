/**
 *  Paradis — Ever growing network for parallel and distributed computing.
 *  Copyright © 2012  Mattias Andrée
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.kth.maandree.paradis.servers;
import se.kth.maandree.paradis.net.*;
import se.kth.maandree.paradis.net.messages.*;
import se.kth.maandree.paradis.io.*;
import se.kth.maandree.paradis.*;


/**
 * Package sharing server
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class PackageServer extends AbstractServer
{
    /**
     * Constructor
     */
    public PackageServer()
    {
        super(-1 ^ (-1 >>> 1));
	
	TransferProtocolRegister.register(String.class, "fetchpkg");
	
	Blackboard.getInstance(null).registerObserver(new Blackboard.BlackboardObserver()
	        {
		    /**
		     * {@inheritDoc}
		     */
		    @Override
		    public void messageBroadcasted(final Blackboard.BlackboardMessage message)
		    {
			if (message instanceof PacketReceived)
			{
			    final Packet packet = ((PacketReceived)message).packet;
			    if (packet.messageType.equals("fetchpkg"))
				System.out.print(packet.message);
			}
		    }
	        });
    }
    
    
    
    /**
     * Packet factory for this server
     */
    private PacketFactory factory = null;
    
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean invoke(final String command, final boolean consumed)
    {
	if ((command.equals("fetchpkg") || command.startsWith("fetchpkg ")) == false)
	    return false;
	if (consumed)
	    return true;
	
	if (this.factory == null)
	{   if (NetworkServer.localUser == null)
	    {
		System.out.println("Please initialise network first:  network init");
		return true;
	    }
	    this.factory = new PacketFactory(NetworkServer.localUser, false, false, (short)32);
	}
	
	Blackboard.getInstance(null).broadcastMessage(new SendPacket(this.factory.createBroadcast(command.substring("fetchpkg ".length()) + '\n', "fetchpkg")));
	
	return true;
    }
    
}

