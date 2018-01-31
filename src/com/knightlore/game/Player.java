package com.knightlore.game;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.knightlore.network.NetworkObject;
import com.knightlore.network.protocol.ClientControl;
import com.knightlore.network.protocol.ServerCommand;
import com.knightlore.network.protocol.ServerControl;
import com.knightlore.network.protocol.ServerProtocol;
import com.knightlore.render.Camera;
import com.knightlore.render.IRenderable;
import com.knightlore.render.Screen;
import com.knightlore.utils.Vector2D;

public class Player extends NetworkObject implements IRenderable {

	private Camera camera;
	private final Map<ServerControl, CameraCommunicatingInterface> mapCorespondantFields; 
	
	public Player(Camera camera) {
		super();
		this.camera = camera;
		this.mapCorespondantFields = new HashMap<>();
		this.mapCorespondantFields.put(ServerControl.XPOS, this.camera::getxPos);
		this.mapCorespondantFields.put(ServerControl.YPOS, this.camera::getyPos);
		this.mapCorespondantFields.put(ServerControl.XDIR, this.camera::getxDir);
		this.mapCorespondantFields.put(ServerControl.YDIR, this.camera::getyDir);
		this.mapCorespondantFields.put(ServerControl.XPLANE, this.camera::getxPlane);
		this.mapCorespondantFields.put(ServerControl.YPLANE, this.camera::getyPlane);
	}


	public Vector2D getPosition() {
		Vector2D pos = new Vector2D(camera.getxPos(), camera.getyPos());
		return pos;
	}

	public Vector2D getDirection() {
		Vector2D dir = new Vector2D(camera.getxDir(), camera.getyDir());
		return dir;
	}

	public Camera getCamera() {
		return camera;
	}
	
	public void setInputState(Map<ClientControl, Byte> inputState) {
	    camera.setInputState(inputState);
	}

    @Override
    public void render(Screen s, int x, int y) {
        s.fillRect(0x000000, (int) this.position.getX(),
                (int) this.position.getY(), 10, 50);
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onUpdate() {
    	if(camera!=null)
    		camera.update();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        
    }
    
    public byte[] serialize(){
		byte[] thisState = new byte[ServerProtocol.TOTAL_LENGTH];

		// Prepend metadata to the state array.
		byte[] metadata = ServerProtocol.getMetadata();
		for (int i = 0; i < ServerProtocol.METADATA_LENGTH; i++) {
			thisState[i] = metadata[i];
		}
		
		byte[] playerID = ServerProtocol.uuidAsBytes(super.objectUniqueID);
		//add player id to the packet
		for(int i=ServerProtocol.METADATA_LENGTH; i<ServerProtocol.METADATA_LENGTH+ServerProtocol.PLAYERID_LENGTH; i++){
			thisState[i] = playerID[i-ServerProtocol.METADATA_LENGTH];
		}

		//adding player stats from camera class
		int loopsNumber = 0;
		try {
			for (int i = ServerProtocol.MESSAGE_STARTING_POINT; i < ServerProtocol.TOTAL_LENGTH;) {
				// taking the current control
				int positionCurrentCommand = loopsNumber;
				ServerControl currentControl = ServerProtocol.getControlByPosition(positionCurrentCommand);
				Integer[] indexes = ServerProtocol.getIndexesByControl(currentControl);
				
				CameraCommunicatingInterface cameraReference = this.mapCorespondantFields.get(currentControl);
			    double cameraResult = cameraReference.accessDataFromCamera();
				int startingIndex = indexes[0];
				int endingIndex = indexes[1];
				byte[] convertedCameraValue = ServerProtocol.doubleToByteArray(cameraResult);
				
				for(int j = startingIndex + ServerProtocol.MESSAGE_STARTING_POINT; j<endingIndex + ServerProtocol.MESSAGE_STARTING_POINT; j++){
					thisState[j] = convertedCameraValue[j-startingIndex-ServerProtocol.MESSAGE_STARTING_POINT];
				}
				i += endingIndex-startingIndex;
				loopsNumber++;
				
			}
		} catch (IOException e) {
			System.err.println("Index not good...");
			e.printStackTrace();
		}
		return thisState;

    }
    
	@Override
	public ServerCommand deserialize(byte[] packet) {
		try {
			//creating the initial map
			Map<ServerControl, Double> objectStats = new HashMap<ServerControl, Double>();
			
			// Metadata processing.
			ByteBuffer buf = ByteBuffer.wrap(packet);
			long timeSent = buf.getLong();

			// set the new position in order to read objectid
			buf.position(ServerProtocol.METADATA_LENGTH);
			byte[] objectIdByte = new byte[ServerProtocol.PLAYERID_LENGTH];
			buf.get(objectIdByte, 0, ServerProtocol.PLAYERID_LENGTH);

			UUID playerID = ServerProtocol.bytesAsUuid(objectIdByte);

			buf.position(ServerProtocol.MESSAGE_STARTING_POINT);

			int position = 0;
			while (buf.hasRemaining()) {
				ServerControl currentControl = ServerProtocol.getControlByPosition(position);
				double valueOfCurrentControl = buf.getDouble(buf.position());
				objectStats.put(currentControl, valueOfCurrentControl);
				buf.position(buf.position() + ServerProtocol.DOUBLE_TO_BYTES_LENGTH);
				position++;
			}
			return new ServerCommand(timeSent, playerID, objectStats);

		} catch (IOException e) {
			System.err.println("Bad index...");
		}
		return null;
	}
	
	@FunctionalInterface
    public static interface CameraCommunicatingInterface{
        double accessDataFromCamera();
    }

}
