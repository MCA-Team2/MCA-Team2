import random
import pickle
import matplotlib.pyplot as plt
import numpy as np
import heartpy as hp
from sklearn.preprocessing import StandardScaler
from sklearn.neural_network import MLPClassifier
from sklearn.tree import DecisionTreeClassifier
from sklearn.neighbors import KNeighborsClassifier
from sklearn.model_selection import cross_val_score
from sklearn.metrics import roc_curve
from sklearn.model_selection import train_test_split
from sklearn.model_selection import GridSearchCV
from sklearn.ensemble import GradientBoostingClassifier
from sklearn.naive_bayes import GaussianNB
from sklearn.metrics import accuracy_score
from sklearn.metrics import confusion_matrix, classification_report
from sklearn_porter import Porter

#어느정도 성능이 나오는데 높이고 싶다면 GridSearchCV사용하시면 됩니다.
# https://tobigs.gitbook.io/tobigs/data-analysis/python-knn 참고

# 데이터들 변환(스케일링) - 성능이 안나오면 이 함수로 모든 data의 크기를 0~1로  standarization(scailing)
# scaler = StandardScaler() # 생성자 호출 - 변환기 객체 생성
# scaler.fit(X_train, y_train) # 학습 데이터의 평균과 표준 편차를 데이터 변환할 때 이용하기 위해서
# X_train_transformed = scaler.transform(X_train) # 학습 데이터 세트 변환
# X_test_transformed = scaler.transform(X_test) # 테스트 데이터 세트 변환

#cross validation metric 출력
def print_metrics(model, X_train, y_train):
    scores = cross_val_score(model, X_train, y_train, cv=10)
    print('*** Cross val score *** \n   {}'.format(scores))
    print('\n*** Mean Accuracy *** \n   {:.7f}'.format(scores.mean()))
    # print('\n*** Confusion Matrix *** \n', confusion_matrix(y_train, model.predict(X_train)))

#test dataset metric 출력
def print_test_metrics(model, X_test, y_test):
    print('*** Test Accuracy *** \n   {}'.format(model.score(X_test, y_test)))
    print('\n*** Confusion Matrix *** \n', confusion_matrix(y_test, model.predict(X_test)))

#기타  metric 보고싶을때 f1score etc
def acc_score(Y, y_pred):
    print('model')
    print("acc:", accuracy_score(Y, y_pred))
    print(classification_report(Y, y_pred))

with open("X.pickle","rb") as fr:
    X = pickle.load(fr)

with open("Y.pickle","rb") as fr:
    Y = pickle.load(fr)

import sys
np.set_printoptions(threshold=sys.maxsize, precision=2, suppress=True)
Z = (np.concatenate((X, Y), axis=1))
Z = Z[np.squeeze(Y) == 1]
print(Z)

np.random.seed(13)

arr = np.arange(X.shape[0])
np.random.shuffle(arr)
X, Y = X[arr], Y[arr]
cutoff = int(X.shape[0] * 0.8)
X_test = X[cutoff:]
Y_test = Y[cutoff:]
X_train = X[:cutoff]
Y_train = Y[:cutoff]

NB = GaussianNB()
DTC = DecisionTreeClassifier(random_state=156)
KNN = KNeighborsClassifier()
GBC = GradientBoostingClassifier(random_state=0)
# MLP = MLPClassifier(solver='lbfgs', random_state=0)
NBmodel = NB.fit(X_train, Y_train)
DTCmodel = DTC.fit(X_train, Y_train)
KNNmodel = KNN.fit(X_train, Y_train)
GBCmodel = GBC.fit(X_train, Y_train)
# model = DCT.fit(X_train, Y_train)

print(DTCmodel.predict([[103.042, 2110.308, 2.60, 4.764803]]))

porter = Porter(DTC, language='java')
output = porter.export(embed_data=True)
with open("output.java", "w") as f:
    f.write(output)

myModel = DTCmodel
# corss validation을 통해 평균 acc구해보기
print_metrics(myModel, X_train, Y_train) 
# test dataset에 대해서 metric 구해보기
print_test_metrics(myModel, X_test, Y_test)
#기타 metric
acc_score(Y_test, myModel.predict(X_test))

fpr, tpr, thre = roc_curve(Y_test, NBmodel.predict(X_test))
plt.plot(fpr, tpr, label="NaiveBayes")

fpr, tpr, thre = roc_curve(Y_test, DTCmodel.predict(X_test))
plt.plot(fpr, tpr, label="DecisionTree")

fpr, tpr, thre = roc_curve(Y_test, KNNmodel.predict(X_test))
plt.plot(fpr, tpr, label="KNearestNeighbor")

fpr, tpr, thre = roc_curve(Y_test, GBCmodel.predict(X_test))
plt.plot(fpr, tpr, label="GradientBoosting")

plt.xlim([0.0, 1.0])
plt.ylim([0.0, 1.0])
plt.rcParams['font.size'] = 12
plt.title('ROC curve')
plt.xlabel('False Positive Rate (1 - Specificity)')
plt.ylabel('True Positive Rate (Sensitivity)')
plt.legend(loc="lower right", fontsize=10)
plt.grid(True)

plt.show()