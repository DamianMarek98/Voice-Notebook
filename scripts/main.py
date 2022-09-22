from typing import Optional

import jiwer
from Levenshtein import distance
from fastapi import FastAPI
from pydantic import BaseModel
from jiwer import wer

app = FastAPI()


class CompareRequest(BaseModel):
    originalText: str
    toCompare: str

class Result(BaseModel):
    wer: Optional[float]
    lev: Optional[float]


@app.post("/compare", response_model=Result)
async def root(request: CompareRequest):
    transformation = jiwer.Compose([
        jiwer.ToLowerCase(),
        jiwer.RemoveWhiteSpace(replace_by_space=True),
        jiwer.RemoveMultipleSpaces(),
        jiwer.RemovePunctuation(),
        jiwer.RemoveKaldiNonWords(),
        jiwer.ReduceToListOfListOfWords(word_delimiter=" ")
    ])


    res = Result()
    res.wer = wer(request.originalText, request.toCompare, truth_transform=transformation, hypothesis_transform=transformation)
    res.lev = distance(request.originalText, request.toCompare)
    return res

