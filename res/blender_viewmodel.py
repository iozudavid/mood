import bpy
import mathutils
import math
import os

OBJ_NAME = "Shotgun.001"

width = 128
height = 128
cam = bpy.data.cameras["Camera"]
view_obj = bpy.data.objects[OBJ_NAME]
cam_obj = bpy.data.objects["Camera"]
bpy.context.scene.render.resolution_x = width
bpy.context.scene.render.resolution_y = height
bpy.context.scene.render.use_antialiasing = False
outputDir = os.path.dirname(bpy.data.filepath)
fileName = "sg_view_"
oldRotMode = view_obj.rotation_mode
baseRot = view_obj.rotation_quaternion

NUM_DIRS = 32
NUM_FRAMES = 1
DIST = 8
baseAng = 360.0/NUM_DIRS


def take_snapshot(num, dist):
    position = mathutils.Vector((-dist,0,0))
    cam_obj.location = position
    direction = -position
    filePath = (outputDir+"/"+fileName+str(num))
    bpy.context.scene.render.filepath = filePath
    bpy.ops.render.render(write_still=True)
    print(angle)


take_snapshot(0,DIST)

print("Done")
