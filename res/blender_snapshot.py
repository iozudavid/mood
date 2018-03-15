import bpy
import mathutils
import math
import os

width = 128
height = 128
cam = bpy.data.cameras["Camera"]
cam.type = "ORTHO"
cam_obj = bpy.data.objects["Camera"]
#pivot_obj = bpy.data.objects["Turret_Pivot"]
bpy.context.scene.render.resolution_x = width
bpy.context.scene.render.resolution_y = height
bpy.context.scene.render.use_antialiasing = False
outputDir = os.path.dirname(bpy.data.filepath)
fileName = "pl_shotgun_"

NUM_DIRS = 32
baseAng = 360.0/NUM_DIRS

def take_snapshot(num, dist):
    angle = num * baseAng
    eulerAngles = mathutils.Euler((0,0,math.radians(angle)),"XYZ")
    #pivot_obj.rotation_euler = eulerAngles
    position = mathutils.Vector((-dist,0,0))
    position.rotate(eulerAngles)
    cam_obj.location = position
    direction = -position
    rotation_quat = direction.to_track_quat("-Z","Y")
    cam_obj.rotation_euler = rotation_quat.to_euler()
    filePath = (outputDir+"/"+fileName+str(num))
    bpy.context.scene.render.filepath = filePath
    bpy.ops.render.render(write_still=True)
    print(angle)

for i in range(0,NUM_DIRS):
    take_snapshot(i,8)
    
print("Done")
