positionScore();

function positionScore() {
    let w = window.innerWidth;
    let h = window.innerHeight;
    let score = document.getElementById("score");
    if(w > h) {
        //document.getElementById("score").style.top = 0;
        score.classList.add("leftScore");
        score.classList.remove("bottomScore");
    } else {
        score.classList.add("bottomScore");
        score.classList.remove("leftScore");
    }

    score.classList.remove("invisible");
}

onresize = (event) => {
    positionScore();
    positionElements(document.querySelectorAll(".rack .draggable"));

};
