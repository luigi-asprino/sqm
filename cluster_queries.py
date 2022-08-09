import csv
from scipy import sparse
import numpy as np
from scipy.sparse import csr_matrix
from sklearn.cluster import KMeans

rows, columns = 1343197, 12908
matrix = sparse.lil_matrix((rows, columns))

csvreader = csv.reader(open('/Users/lgu/Desktop/bioportal/extracted_features.csv'))
row = []
col = []
data = []
for line in csvreader:
    row.append(int(line[0]))
    col.append(int(line[1]))
    data.append(int(line[2]))


csvreader_queries = csv.reader(open('/Users/lgu/Desktop/bioportal/queries.csv'))
queries = {}
for line in csvreader_queries:
    queries[int(line[0])] = line[1]

row_m = np.array(row)
col_m = np.array(col)
data_m = np.array(data)
X = csr_matrix((data_m, (row_m, col_m)), shape=(rows, columns))

#print(X)

# ks = [2, 4, 8, 16, 32, 64, 128, 256, 512]
#
# for k in ks:
#     kmeans = KMeans(n_clusters=k, random_state=0).fit(X)
#     print(f"{k}\t{kmeans.inertia_}")

kmeans = KMeans(n_clusters=128, random_state=0).fit(X)
queries_by_cluster = {}
for i in range(0, 128):
   queries_by_cluster[i] = []

for idx, cluster in enumerate(kmeans.labels_):
   #print(f"{idx} {cluster}")
   queries_by_cluster[int(cluster)].append(queries[idx])

# print(queries_by_cluster)

with open('example.csv', 'w') as file:
    writer = csv.writer(file)
    writer.writerow(data)

out_file_writer = csv.writer(open(f"/Users/lgu/Desktop/bioportal/clusters.csv", "w"))

for i in range(0, 128):

    for q in queries_by_cluster[i]:
        out_file_writer.writerow([i, q])

    f = open(f"/Users/lgu/Desktop/bioportal/clusters_1000/cluster_{i}.txt", "w")
    print(f"Cluster #{i}: size ({len(queries_by_cluster[i])})")
    for q in queries_by_cluster[i]:
        f.write(q)
        f.write("\n-\n")

    f.flush()
    f.close()

