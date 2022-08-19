import argparse
import csv
import os
import pickle

import matplotlib.pyplot as plt
import numpy as np
from scipy import sparse
from scipy.sparse import csr_matrix
from sklearn.cluster import KMeans

parser = argparse.ArgumentParser(description='..')

parser.add_argument('--input_folder', dest='input_folder', help=' ... ')
parser.add_argument('--rows', dest='rows', type=int, help=' ... ')
parser.add_argument('--columns', dest='columns', type=int, help=' ... ')
parser.add_argument('--kmax', dest='kmax', type=int, help=' ... ')
parser.add_argument('--step', dest='step', type=int, help=' ... ')
parser.add_argument('--min', dest='min', type=int, help=' ... ')
args = parser.parse_args()

input_folder = args.input_folder
rows, columns = args.rows, args.columns
kmax = args.kmax
step = args.step
min = args.min

matrix = sparse.lil_matrix((rows, columns))

csvreader = csv.reader(open(f'{input_folder}/extracted_features.csv'))
row = []
col = []
data = []
for line in csvreader:
    row.append(int(line[0]))
    col.append(int(line[1]))
    data.append(int(line[2]))

row_m = np.array(row)
col_m = np.array(col)
data_m = np.array(data)
X = csr_matrix((data_m, (row_m, col_m)), shape=(rows, columns))

sse = {}

for k in range(min, kmax + 1, step):
    if min <= 0:
        continue
    kmeans_file = f"{input_folder}/kmeans_{k}.p"
    if os.path.exists(kmeans_file):
        print(f"Loading K-Means with k {k}")
        kmeans = pickle.load(open(kmeans_file, "rb"))
    else:
        print(f"Computing K-Means with k {k}")
        kmeans = KMeans(n_clusters=k, random_state=0).fit(X)
    print(f"{k}\t{kmeans.inertia_}")
    sse[k] = kmeans.inertia_
    pickle.dump(kmeans, open(kmeans_file, "wb"))

plt.figure()
plt.plot(list(sse.keys()), list(sse.values()))
plt.xlabel("Number of clusters")
plt.ylabel("SSE")
plt.savefig(f"{input_folder}/elbow.png")
plt.clf()
