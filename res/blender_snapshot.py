import bpy
import mathutils
import math
import os

width = 128
height = 128
cam = bpy.data.cameras["Camera"]
cam.type = "ORTHO"
cam_obj = bpy.data.objects["Camera"]
bpy.context.scene.render.resolution_x = width
bpy.context.scene.render.resolution_y = height
bpy.context.scene.render.use_antialiasing = False
outputDir = os.path.dirname(bpy.data.filepath)
fileName = "pistol_"

isAnim = False

NUM_DIRS = 32
NUM_FRAMES = 1
DIST = 8
baseAng = 360.0/NUM_DIRS


def take_snapshot(num, dist, animFrame):
    angle = num * baseAng
    eulerAngles = mathutils.Euler((0,0,math.radians(angle)),"XYZ")
    position = mathutils.Vector((-dist,0,0))
    position.rotate(eulerAngles)
    cam_obj.location = position
    direction = -position
    rotation_quat = direction.to_track_quat("-Z","Y")
    cam_obj.rotation_euler = rotation_quat.to_euler()
    if(isAnim == True):
        filePath = (outputDir+"/"+fileName+str(num)+animFrame)
    else:
        filePath = (outputDir+"/"+fileName+str(num))
    bpy.context.scene.render.filepath = filePath
    bpy.ops.render.render(write_still=True)
    print(angle)


def doAnim():
    # compute frame step
    frameStep = bpy.context.scene.frame_end / NUM_FRAMES
    for fr in range (0,NUM_FRAMES):
        print("taking frame "+str(fr))
        # set the frame
        frameName = "_f"+str(fr)
        frameNum = fr * frameStep
        bpy.context.scene.frame_set(frameNum)
        # take pictures
        for i in range(0,NUM_DIRS):
            take_snapshot(i,DIST,frameName)


doAnim()
print("Done")
