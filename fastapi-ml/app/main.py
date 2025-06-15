from fastapi import FastAPI

app = FastAPI()

@app.get("/")
def read_root():
    return {"message": "Hello FastAPI!"}

@app.post("/predict")
def predict(data: dict):
    # 여기에 PyTorch 모델 예측 코드 들어감
    return {"result": "dummy result"}