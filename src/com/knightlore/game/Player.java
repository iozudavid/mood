package com.knightlore.game;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.knightlore.game.entity.weapon.Shotgun;
import com.knightlore.game.entity.weapon.Weapon;
import com.knightlore.network.NetworkObject;
import com.knightlore.network.protocol.ClientControl;
import com.knightlore.network.protocol.ServerCommand;
import com.knightlore.network.protocol.ServerControl;
import com.knightlore.network.protocol.ServerProtocol;
import com.knightlore.render.Camera;
import com.knightlore.render.IRenderable;
import com.knightlore.render.PixelBuffer;
import com.knightlore.utils.Vector2D;

public class Player extends NetworkObject implements IRenderable {

	private Camera camera;
	private Weapon currentWeapon;
	
	private final Map<ServerControl, CameraGetterInterface> controlGettersMap;
	private final Map<ServerControl, CameraSetterInterface> controlSettersMap;
	private static boolean finished = false;

	public Player(UUID uuid, Camera camera) {
		super(uuid);
		assert(camera != null);
		
		this.camera = camera;
		this.currentWeapon = new Shotgun();
		
		this.controlGettersMap = new HashMap<>();
		this.controlGettersMap.put(ServerControl.XPOS, this.camera::getxPos);
		this.controlGettersMap.put(ServerControl.YPOS, this.camera::getyPos);
		this.controlGettersMap.put(ServerControl.XDIR, this.camera::getxDir);
		this.controlGettersMap.put(ServerControl.YDIR, this.camera::getyDir);
		this.controlGettersMap.put(ServerControl.XPLANE,
				this.camera::getxPlane);
		this.controlGettersMap.put(ServerControl.YPLANE,
				this.camera::getyPlane);

		this.controlSettersMap = new HashMap<>();
		this.controlSettersMap.put(ServerControl.XPOS, this.camera::setxPos);
		this.controlSettersMap.put(ServerControl.YPOS, this.camera::setyPos);
		this.controlSettersMap.put(ServerControl.XDIR, this.camera::setxDir);
		this.controlSettersMap.put(ServerControl.YDIR, this.camera::setyDir);
		this.controlSettersMap.put(ServerControl.XPLANE,
				this.camera::setxPlane);
		this.controlSettersMap.put(ServerControl.YPLANE,
				this.camera::setyPlane);
		Player.this.finished = true;
		
	}

	public Vector2D getPosition() {
		Vector2D pos = new Vector2D(camera.getxPos(), camera.getyPos());
		return pos;
	}

	public Vector2D getDirection() {
		Vector2D dir = new Vector2D(camera.getxDir(), camera.getyDir());
		return dir;
	}

	public void setInputState(Map<ClientControl, Byte> inputState) {
		camera.setInputState(inputState);
	}

	@Override
	public void render(PixelBuffer pix, int x, int y) {
		pix.fillRect(0x000000, (int) this.position.getX(),
				(int) this.position.getY(), 10, 50);
	}


	@Override
	public void onUpdate() {
	}


	@Override
	public byte[] serialize(boolean disconnect) {
		if (camera == null) {
			System.out.println("Camera started null");
			return null;
		}
		if(Player.this.finished==false){
			System.out.println("Constructor didn't finish");
			return null;
		}
		
		byte[] thisState = new byte[ServerProtocol.TOTAL_LENGTH];

		// Prepend metadata to the state array.
		byte[] metadata = ServerProtocol.getMetadata();
		for (int i = 0; i < ServerProtocol.METADATA_LENGTH; i++) {
			thisState[i] = metadata[i];
		}

		byte[] playerID = ServerProtocol.uuidAsBytes(super.objectUniqueID);
		// add player id to the packet
		for (int i = ServerProtocol.METADATA_LENGTH; i < ServerProtocol.METADATA_LENGTH
				+ ServerProtocol.OBJECTID_LENGTH; i++) {
			thisState[i] = playerID[i - ServerProtocol.METADATA_LENGTH];
		}

		if(disconnect){
			for (int i = ServerProtocol.MESSAGE_STARTING_POINT; i < ServerProtocol.TOTAL_LENGTH; i++) {
				thisState[i] = ServerProtocol.disconnectedState[i - ServerProtocol.MESSAGE_STARTING_POINT];
			}
			//if disconnect then end it here with return statement
			return thisState;
		}
		
		// adding player stats from camera class
		int loopsNumber = 0;
		try {
			for (int i = ServerProtocol.MESSAGE_STARTING_POINT; i < ServerProtocol.TOTAL_LENGTH;) {
				// taking the current control
				int positionCurrentCommand = loopsNumber;
				ServerControl currentControl = ServerProtocol
						.getControlByPosition(positionCurrentCommand);
				Integer[] indexes = ServerProtocol
						.getIndexesByControl(currentControl);
				int startingIndex = indexes[0];
				int endingIndex = indexes[1];

				CameraGetterInterface cameraReference = this.controlGettersMap
						.get(currentControl);
				if (cameraReference == null)
					return null;
				double cameraResult = cameraReference.accessDataFromCamera();
				byte[] convertedCameraValue = ServerProtocol
						.doubleToByteArray(cameraResult);

				for (int j = startingIndex
						+ ServerProtocol.MESSAGE_STARTING_POINT; j < endingIndex
								+ ServerProtocol.MESSAGE_STARTING_POINT; j++) {
					thisState[j] = convertedCameraValue[j - startingIndex
							- ServerProtocol.MESSAGE_STARTING_POINT];
				}
				i += endingIndex - startingIndex;
				loopsNumber++;

			}
		} catch (IOException e) {
			System.err.println("Index not good...");
			e.printStackTrace();
		}
		return thisState;

	}

	@Override
	public void deserialize(ServerCommand command) {
		// TODO: only deserialise if the command's timestamp is recent enough.
		Double val;
		for (ServerControl c : ServerControl.values())
			if ((val = command.getValueByControl(c)) != null)
				this.controlSettersMap.get(c).setDataOnCamera(val);

	}

	@FunctionalInterface
	public static interface CameraGetterInterface {
		double accessDataFromCamera();
	}
	public Camera getCamera() {
		return camera;
	}
	
	public Weapon getCurrentWeapon() {
		return currentWeapon;
	}

	@FunctionalInterface
	public static interface CameraSetterInterface {
		void setDataOnCamera(double val);
	}
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		
	}
}
