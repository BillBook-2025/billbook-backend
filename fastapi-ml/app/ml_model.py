# 실제로 예측 담당

# ml_model.py
import joblib
from typing import List

# 이미 학습된 모델 파일을 로드
model = joblib.load("models/trained_model.pkl")

def predict(features: List[float]) -> float:
    # features를 모델에 넣어 예측 결과 리턴
    prediction = model.predict([features])
    return prediction[0]