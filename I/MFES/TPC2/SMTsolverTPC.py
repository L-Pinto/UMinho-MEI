from z3 import *

# Reading input file
restrictions = False

# futoshiki instance, we use '0' for empty cells
matrix = []

# [i1,j1, i2,j2] requires that values[i1,j1] < values[i2,j2]
# Note: 1-based
lt = []

f = open("Input1.txt", "r")
for x in f:
    if (x == '\n'):
        restrictions = True
    elif (restrictions == False):
        linelist = x.split(" ")
        # Define board size 
        N = len(linelist)
        # String to int
        for i in range(0,N):
            linelist[i] = int(linelist[i])

        # Add new line to matrix
        matrix.append(linelist)
    else:
        restriction = x.split(",")
        # String to int
        for i in range(0,4):
            restriction[i] = int(restriction[i])
        # Add new line to matrix
        lt.append(restriction)


# NxN matrix of integer variables
X = [ [ Int("x_%s_%s" % (i+1, j+1)) for j in range(N) ]
      for i in range(N) ]

# each cell contains a value in {1, ..., N}
cells_c  = [ And(1 <= X[i][j], X[i][j] <= N)
             for i in range(N) for j in range(N) ]

# each row contains a digit at most once
rows_c   = [ Distinct(X[i]) for i in range(N) ]

# each column contains a digit at most once
cols_c   = [ Distinct([ X[i][j] for i in range(N) ])
             for j in range(N) ]

futoshiki_c = cells_c + rows_c + cols_c 

instance_c = [ If(matrix[i][j] == 0,
                  True,
                  X[i][j] == matrix[i][j])
               for i in range(N) for j in range(N) ]

s = Solver()
s.add(futoshiki_c + instance_c)
for i in lt:
    s.add(X[i[0]-1][i[1]-1] < X[i[2]-1][i[3]-1])
if s.check() == sat:
    m = s.model()
    r = [ [ m.evaluate(X[i][j]) for j in range(N) ]
          for i in range(N) ]
    print_matrix(r)
else:
    print("failed to solve")