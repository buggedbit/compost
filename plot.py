import numpy as np
import pandas as pd
from mpl_toolkits.mplot3d import Axes3D
import matplotlib.pyplot as plt
import matplotlib as mpl
from matplotlib.backends.backend_pdf import PdfPages

#Read the packing PARTS 
df_parts = pd.read_csv("Packing1.csv")
dataset_parts = df_parts.values

Component = {}
for row in dataset_parts:
    if row[7] not in Component.keys():
        Component[row[7]] = []
    Component[row[7]].append([row[0], row[1:4].astype(np.float16), row[4:7].astype(np.float16)])

#Read the BOX sizes 
df_boxes = pd.read_csv("Boxes.csv")
dataset_boxes = df_boxes.values
    
box_name = dataset_boxes[:,0].tolist()
Box = {}
for row in dataset_boxes:
    Box[row[0]] = row[1:4].astype(np.float16)

#Get a list of unique PARTS and assign Unique colors to them 
part_name = dataset_parts[:,0].tolist()
unique_part_name = set()
for p in part_name:
    unique_part_name.add(p)
    
df = pd.read_csv("Colors.csv")
dataset = df.values
color = dataset[:,1].tolist()

color_coding = {}
k = 6
for n in unique_part_name:
    color_coding[n] = color[k]
    k = (k + 1)%len(color)

def setup(i):
    axis = fig.add_subplot(2, 2, i, projection='3d')
    axis.invert_yaxis()
    return axis
def draw_box(axis,size):
    x, y, z = size

    axis.set_xlabel('X')
    axis.set_xlim(0,
                  x)
    axis.set_ylabel('Y')
    axis.set_ylim(0,y)
    axis.set_zlabel('Z')
    axis.set_zlim(0,z)
    draw_part(axis, [0,0,0], size, '#202020', True)
    return axis
    


def draw_part(axis, lbb, size, c, isBox = False):
    lbb_x, lbb_y, lbb_z = lbb
    dim_x, dim_y, dim_z = size

    x = np.linspace(lbb_x,lbb_x + dim_x,num=10)
    y = np.linspace(lbb_y,lbb_y + dim_y,num=10)
    z = np.linspace(lbb_z,lbb_z + dim_z,num=10)

    x1, z1 = np.meshgrid(x, z)
    y11 = np.ones_like(x1)*(lbb_y)
    y12 = np.ones_like(x1)*(lbb_y + dim_y)

    x2, y2 = np.meshgrid(x, y)
    z21 = np.ones_like(x2)*(lbb_z)
    z22 = np.ones_like(x2)*(lbb_z + dim_z)

    y3, z3 = np.meshgrid(y, z)
    x31 = np.ones_like(y3)*(lbb_x)
    x32 = np.ones_like(y3)*(lbb_x + dim_x)

    alp = 0.6  
    lw = 1.5
    eg = "black"
    if (isBox):
        alp = 0.01
        lw  = 2.5
        eg = "#777777"
    # outside surface
    axis.plot_surface(x1, y11, z1, linewidth=lw/2.0, rcount=1, ccount=1, color=c, shade=False, edgecolor=eg, alpha = alp)
    # inside surfaceeg
    axis.plot_surface(x1, y12, z1, linewidth=lw, rcount=1, ccount=1, color=c, shade=False, edgecolor=eg, alpha = alp)
    # bottom surfaceeg
    axis.plot_surface(x2, y2, z21, linewidth=lw/2.0, rcount=1, ccount=1, color=c, shade=False, edgecolor=eg, alpha = alp)
    # upper surfaceeg
    axis.plot_surface(x2, y2, z22, linewidth=lw, rcount=1, ccount=1, color=c, shade=False, edgecolor=eg, alpha = alp)
    # left surfaceeg
    axis.plot_surface(x31, y3, z3, linewidth=lw/2.0, rcount=1, ccount=1, color=c, shade=False, edgecolor=eg, alpha = alp)
    # right surfaceeg
    axis.plot_surface(x32, y3, z3, linewidth=lw, rcount=1, ccount=1, color=c, shade=False, edgecolor=eg, alpha = alp)
    return axis

def draw_legend(fig,color_coding, box_id):
    unique_part = set()
    for part in  Component[box_id]:
        unique_part.add(part[0])
    
    lst1 = []; lst2 = []
    for p in unique_part:     
        fake2Dline = mpl.lines.Line2D([0],[0], linestyle="none", c=color_coding[p], marker = 'o')
        label = p
        lst1.append(fake2Dline)
        lst2.append(label)
    
    fig.legend(lst1, lst2, numpoints = 1,loc=4)
    return fig

#def save_pdf(axis, fig, pp,box):
    #fig.suptitle(box + "Perspective View")
    #pp.savefig(fig)
    
    #axis.view_init(elev=0., azim=0.)
    #fig.suptitle(box + "Front View")
    #pp.savefig(fig)
    
    #axis.view_init(elev=90., azim=0.)
    #fig.suptitle(box + "Top View")
    #pp.savefig(fig)
    

mpl.rcParams.update({'font.size': 10})
pp = PdfPages("Packing.pdf")    
Views = [("Perspective View",45.,45.),("Front View",0.,0.),("Top View",90.,0.),("Side View",0.,270.)]
for name in box_name: 
    num = 1
    key = name + "-" + str(num)
    while(key in Component):
        fig = plt.figure(figsize=(12.0, 7.0))
        for i in range(len(Views)): 
            axis = setup(i+1)
            axis = draw_box(axis, Box[name])
            
            for part in Component[key]:
                axis = draw_part(axis, part[2], part[1], color_coding[part[0]])
            #save_pdf(axis, fig, pp, key)
            axis.view_init(elev=Views[i][1], azim=Views[i][2])
            axis.set_title(Views[i][0])
        fig.suptitle(key)    
        fig = draw_legend(fig, color_coding, key)        
        pp.savefig(fig)
        plt.gcf().clear()
        num = num + 1
        key = name + "-" + str(num)
pp.close()