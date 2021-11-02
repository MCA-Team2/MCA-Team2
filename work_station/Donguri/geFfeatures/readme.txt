<설명>
1. HR
2. pnn50
3. RMSSD

구하는 알고리즘을 자바파일로 만들어놓았습니다.

우선 interval 값은 IBI.csv 파일을 이용했습니다. (WESAD.zip -> WESAD 폴더 -> S4  폴더-> S4_E4_data.zip -> IBI.csv) //여기서 4는 실험참가자의 번호를 의미

이 dataset은 WESAD data로써 
1열은 측정한 시간(크게 의미없는 값, 어짜피 peak간 interval 시간을 알기때문에)
2열은 실제 RR(NN) interval값을 의미합니다.

즉 위에 언급한것처럼 2열만 data로 이용했고, 이 데이터 형식을 이용하여 위 값을 구하는 것을 구현했습니다.

뒷부분 잘 부탁드립니다. 혹시 필요하신 부분이 있으면 말씀해주세요.




<code flow>

class calValues {
	func getValues() : 3가지 feature을 구하는 함수 (overhead를 줄이려고 하나의 함수안에서 짰습니다. class 상속하면서 짜려했는데 디자인하는데 좀 오래걸릴거같아서 그냥 주먹구구식으로 짰습니다.)
	{

		HRResult : HR을 담는 list
		pnn50Result : pnn을 담는 lsit
		RMSSDResult : RMSSD를 담는 list

		위의 세개를 각각 구해서

		total이라는 2D list에 각각의 list를 넣고 return합니다.
		//알고리즘은 주석을 참고하시고 애매한 부분이 있으면 여쭤보세요
		// 모든 feature들을 2분기준(120초)로 window를 씌운 구간에서 값을 구했으며, 
		// 1개의 interval간격으로 window를 shift 하며 값을 구했습니다.
		// 시간이 오래걸리지않길래 이렇게 했습니다.
	}


}

class READCSV : 말그래도 csv 파일 읽는 클래스이자 함수들이 들어있습니다.
