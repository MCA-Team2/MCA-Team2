import pickle
import numpy as np
import pandas as pd
import os
import heartpy as hp
from matplotlib import pyplot as plt
from scipy.ndimage import uniform_filter1d

from sklearn.preprocessing import StandardScaler
from sklearn.tree import DecisionTreeClassifier
from sklearn.neighbors import KNeighborsClassifier
from sklearn.model_selection import cross_val_score
from sklearn.metrics import *
from sklearn.model_selection import train_test_split
from sklearn.model_selection import GridSearchCV
from sklearn.ensemble import GradientBoostingClassifier
from sklearn.naive_bayes import GaussianNB
from sklearn.metrics import accuracy_score
from sklearn.metrics import confusion_matrix, classification_report

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
    print("acc:", accuracy_score(Y_list[1], y_pred2))
    print(classification_report(Y_list[1], y_pred2))


PKL_PATH = './pkl'

def read(pklpath):  
    with open(pklpath, 'rb') as fp:
        dataset = pickle.load(fp,encoding="bytes")
    return dataset

def makedataset():
    dirlist = os.listdir(PKL_PATH)
    
    dirlist = np.array(dirlist)
    index=[]
    
    for i, d in enumerate(dirlist) : 
        if os.path.splitext(d)[1]!='.pkl':
            index.append(i)
                
    datacollect=[]
    dirlist =np.delete(dirlist,index)

    for d in dirlist:
        print(d)
        datacollect.append(read(os.path.join(PKL_PATH, d)))
        
    return datacollect

def extractData(dataset):
    X=dataset[b'signal'][b'wrist'][b'BVP']
    Y=dataset[b'label']
        
    return X, Y

def drop(Y_new):
    ##혹은 not defined (=0) 을 제거하고 진행
    Y_new = pd.DataFrame(Y_new)
    Y_new.iloc[ Y_new < 2 ] = 0 
    Y_new.iloc[ Y_new > 2 ] = 0 
    Y_new.iloc[ Y_new == 2 ] = 1 
    
    return Y_new

trainBVP = []
trainIBI = []
trainLabel = []

datacollect=makedataset()

X = []
Y = []

for d in datacollect:
    tmp1, tmp2 = extractData(d)
    X_list = np.squeeze(np.array(pd.DataFrame(tmp1)))
    Y_list = np.squeeze(np.array(drop(tmp2)))
    assert len(X_list) * 700 == len(Y_list) * 64
    
    WINDOW_TIME = 300
    SLIDE_TIME = 30

    indexBVP = 0
    indexBVPLen = 64 * WINDOW_TIME
    indexBVPStride = 64 * SLIDE_TIME

    indexStress = 0
    indexStressLen = 700 * WINDOW_TIME
    indexStressStride = 700 * SLIDE_TIME

    while indexBVPLen + indexBVP <= X_list.size:
        bvp = X_list[indexBVP: indexBVPLen + indexBVP]
        bvpFilt = hp.filter_signal(bvp, [0.7, 3.5], sample_rate=64, order=2, filtertype='bandpass')
        bvpScale = bvpFilt / np.sqrt(uniform_filter1d(bvpFilt * bvpFilt, 64 * 5))
        
        try:
            wd, m = hp.process(bvpScale, sample_rate=64, clean_rr=True, high_precision=True, calc_freq=True)
            ibi = np.array(wd['RR_list_cor'])
            label = Y_list[indexStress: indexStressLen + indexStress]
            if min(label) == max(label) and ibi.size > 100:
                label = int(min(label))
                XFeature = np.array([m['bpm'], m['rmssd'], m['pnn50'], m['lf/hf']])
                print(XFeature)
                YFeature = np.array([label])
                X.append(XFeature)
                Y.append(YFeature)
        except:
            pass

        indexBVP += indexBVPStride
        indexStress += indexStressStride

with open("X.pickle", "wb") as w:
    pickle.dump(np.array(X), w)
with open("Y.pickle", "wb") as w:
    pickle.dump(np.array(Y), w)