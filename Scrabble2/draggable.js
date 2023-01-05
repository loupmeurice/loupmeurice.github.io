
const RACK_SHIFT = 40;
const RACK_LEFT_START = 38;
dragElements(document.querySelectorAll(".rack .draggable"));
positionElements(document.querySelectorAll(".rack .draggable"));

dragMiniElements(document.querySelectorAll(".board .draggable"));

function getRackShift() {
	let rackWidth = document.getElementsByClassName("rack")[0].clientWidth - 20;
	let tileWidth = document.querySelector(".rack .draggable").clientWidth;
	let nbOfTiles = controller.model.nb_of_rack_riles;
	let shift = (rackWidth - (tileWidth * nbOfTiles))/(nbOfTiles + 1);

	return shift;
}

function dragElements(elements) {

	for(let i = 0; i < elements.length; i++)
		dragElement(elements[i]);
}

// position tiles on the rack
function positionElements(elements) {
	for(let i = 0; i < elements.length; i++)
		resetTilePosition(elements[i]);
};

function resetTilePosition(el) {
	let position = el.getAttribute("position");
	if(position) {
		el.style.top = "0px";
		//let leftPx = RACK_LEFT_START + (position * RACK_SHIFT);
		let tileWidth = document.querySelector(".rack .draggable").clientWidth;
		let leftPx = ((parseInt(position) + 1) * getRackShift()) + (tileWidth * parseInt(position)) + 10;
		el.style.left = leftPx + "px";
	}
}

function isInvisible(elmnt) {
	return elmnt.querySelector(".playable-tile.transparent") != null;
}

function resetRackAndBoard() {

	// 1. remove all occupied tiles from the board
	let occupiedTiles = document.querySelectorAll("#js-board .tile");
	let positions = [];
	for(const tile of occupiedTiles) {
		let free = tile.getAttribute("free");
		if(free != null && free != 'false') {
			let position = tile.getAttribute("free");
			if(isNaN(position) == false)
				positions.push(parseInt(position));
			tile.querySelector(".draggable").classList.add("invisible");
			tile.removeAttribute("free");
			tile.querySelector(".playable-tile").classList.remove("temporary");
		}
	}

	// 2. get all the tiles back to the rack
	for(const position of positions) {
		let draggableTile = document.querySelector(".rack .draggable[position='" + position + "']")
		draggableTile.querySelector(".playable-tile").classList.remove("transparent");
	}

	// 3. remove any invalid words formed by temporary tiles
	for(const tile of document.querySelectorAll(".board .playable-tile.invalid"))
		tile.classList.remove("invalid");

	// 4. reset the current play score
	controller.displayCurrentPlayScore("");

	controller.removeAllTilesFromBoard();
}

function getClientY(e) {
	if(!e)
		return null;

	if(e.clientY)
		return e.clientY;

	if(e.touches && e.touches[0] && e.touches[0].clientY)
		return e.touches[0].clientY;

	return null;
}

function getClientX(e) {
	if(!e)
		return null;

	if(e.clientX)
		return e.clientX;

	if(e.touches && e.touches[0] && e.touches[0].clientX)
		return e.touches[0].clientX;

	return null;
}

function stopAutoPageScrolling(e) {
	//document.documentElement.style.overflow = 'hidden';
}

function activateAutoPageScrolling(e) {
	//document.documentElement.style.overflow = 'auto';
}

function dragElement(elmnt) {
  let pos1 = 0, pos2 = 0, pos3 = 0, pos4 = 0;
  elmnt.onmousedown = dragMouseDown;
  elmnt.addEventListener('touchstart', dragMouseDown);


  function dragMouseDown(e) {
    e = e || window.event;
    //e.preventDefault();
	stopAutoPageScrolling(e);
	//document.documentElement.style.overflow = 'hidden';


    if(isInvisible(elmnt))
    	return;

    // get the mouse cursor position at startup:
	elmnt.classList.add("alwaysOnTop");

	pos3 = getClientX(e);
	pos4 = getClientY(e);


    document.onmouseup = closeDragElement;
	document.addEventListener('touchend', closeDragElement);
    // call a function whenever the cursor moves:
    document.onmousemove = elementDrag;
	document.addEventListener('touchmove', elementDrag, {passive: false});
  }

  function elementDrag(e) {
	e.preventDefault();
	e.stopPropagation();

    // calculate the new cursor position:
    pos1 = pos3 - getClientX(e);
    pos2 = pos4 - getClientY(e);
    pos3 = getClientX(e);
    pos4 = getClientY(e);
    // set the element's new position:
	let top = elmnt.offsetTop - pos2;
	let left = elmnt.offsetLeft - pos1;

    elmnt.style.top = top + "px";
    elmnt.style.left = left + "px";


	checkIfSwitchTiles();
  }

  function checkIfSwitchTiles() {
	let position = parseInt(elmnt.getAttribute("position"));
	let mustSwitch = false;
	let neighbour = null;
	let newPosition = null;
	if(position > 0) {
		//check left tiles
		for(let i = 1; i <= position && mustSwitch == false; i++) {
			neighbour = elmnt.parentNode.querySelector(".draggable[position='" + (position - i) + "']");
			let center1 = getCenter(elmnt);

			let rect = neighbour.getBoundingClientRect();
			if(isInside(center1, rect)) {
				mustSwitch = true;
				newPosition = position - i;
			}
		}

	}

	if(mustSwitch == false && position < controller.model.rack.length - 1) {
		//check right
		for(let i = 1; position + i < controller.model.rack.length && mustSwitch == false; i++) {
			neighbour = elmnt.parentNode.querySelector(".draggable[position='" + (position + i) + "']");
			let center1 = getCenter(elmnt);
			let rect = neighbour.getBoundingClientRect();
			if(isInside(center1, rect)) {
				mustSwitch = true;
				newPosition = position + 1;
			}
		}

	}

	if(mustSwitch == true) {

		if(newPosition < position) {
			//left switch
			for(let i = position - 1; i >= newPosition; i--) {
				let el = elmnt.parentNode.querySelector(".draggable[position='" + i + "']");
				let temporary = document.querySelector(".board .tile[free='" + i + "']");
				if(temporary != null) {
					temporary.setAttribute("free", "" + (i + 1));
				}
				el.setAttribute("position", "" + (i + 1));
				resetTilePosition(el);
			}
		} else {
			//right switch
			for(let i = newPosition; i > position; i--) {
				let el = elmnt.parentNode.querySelector(".draggable[position='" + i + "']");
				let temporary = document.querySelector(".board .tile[free='" + i + "']");
				if(temporary != null) {
					temporary.setAttribute("free", "" + (i - 1));
				}
				el.setAttribute("position", "" + (i - 1));
				resetTilePosition(el);
			}
		}
		elmnt.setAttribute("position", "" + newPosition);
		//neighbour.setAttribute("position", "" + position);
		//elmnt.setAttribute("position", "" + newPosition);
		//resetTilePosition(neighbour);
	}


  }

  function removeInvalidTiles() {
	  let tiles = document.querySelectorAll('#js-board .tile .invalid');
	  for(const tile of tiles)
		  tile.classList.remove("invalid");
  }

  function closeDragElement(e) {
    // stop moving when mouse button is released:
	elmnt.classList.remove("alwaysOnTop");
	let parent = elmnt.closest(".draggable")
	if(isInBoard(elmnt) == false) {
		resetTilePosition(parent);
	} else {
		let tile = getBoardTile(parent);
		let column = tile[0];
		let row = tile[1];
		let free = tile[2];
		if(free == false)
			resetTilePosition(parent);
		else {
			playTileOnBoard(elmnt.querySelector(".playable-tile"), [column, row]);
		}
	}

	  removeInvalidTiles();

	  let error = controller.isValid();
	  if(error && controller.invalidWord != null) {
	  	for(const letter of controller.invalidWord.letters) {
	  		let tile = document.querySelector('#js-board .tile[data-col="' + letter.coordinates[1] + '"][data-row="' + letter.coordinates[0] + '"] .playable-tile');
	  		tile.classList.add("invalid");
		}
	  } else {
		  removeInvalidTiles();

	  }

	  let currentPlayScore = "";
	  if(!error) {
		  let cnt = controller.getPoints();
		  currentPlayScore = "  (+" + cnt + ")";
	  }
	  controller.displayCurrentPlayScore(currentPlayScore);


	activateAutoPageScrolling(e);
    document.onmouseup = null;
    document.onmousemove = null;
    document.removeEventListener('touchend', closeDragElement);
	document.removeEventListener('touchmove', elementDrag);
  }
  
  function playTileOnBoard(letter, coordinates) {
  	  let dataLetter = letter.getAttribute("data-letter");
	  if(dataLetter == ' ') {
	  	let value = null;
	  	while(value == null) {
			value = prompt("Choisissez la letter", "A-Z");
			if(/^[a-zA-Z]$/.test(value) == false)
				value = null;
		}
		dataLetter = " " + value;
	  }

	  let boardTile = document.querySelector('[data-col="' + coordinates[0] + '"][data-row="' + coordinates[1] + '"]');
	  let tile = boardTile.querySelector(".mini");
	  tile.classList.remove("invisible");
	  let playableTile = tile.querySelector(".playable-tile");
	  playableTile.setAttribute("data-letter", dataLetter);
	  playableTile.classList.add("temporary");
	  boardTile.setAttribute("free", letter.parentNode.getAttribute("position"));
	  resetTilePosition(letter.parentNode);
	  letter.classList.add("transparent");
	  controller.addTileToBoard(coordinates[1], coordinates[0], dataLetter);
  }
  
  function getBoardTile(element) {
	let center = getCenter(element);
	let tiles = document.getElementsByClassName("tile")
	for(let i = 0; i < tiles.length; i++) {
			let tile = tiles[i];
			let rect = tile.getBoundingClientRect();
			if(isInside(center, rect)) {
				let free = tile.getAttribute("free");
				return [parseInt(tile.getAttribute("data-col")), parseInt(tile.getAttribute("data-row")), free == null ? true : (free == "true" ? true : false)];
			}
	}
	return null;
  }
  
  function isInBoard(element) {
	let board = document.getElementsByClassName("board")[0].getBoundingClientRect();
	let center = getCenter(element);
	return isInside(center, board);
  }
  
  function isInside(center, area) {
	let xPoint = center[0];
	let yPoint = center[1];
	return xPoint >= area.left && xPoint <= area.right && yPoint >= area.top && yPoint <= area.bottom  
  }
  
  
}

function getCenter(element) {
	let rect = element.getBoundingClientRect();
	let xPoint = (rect.right - rect.left)/2 + rect.left;
	let yPoint = (rect.bottom - rect.top)/2 + rect.top;
	return [xPoint, yPoint];
  }

/* Mini elements */
function dragMiniElements(elements) {
	for(let i = 0; i < elements.length; i++)
		dragMiniElement(elements[i]);
}

function dragMiniElement(elmnt) {
  let pos1 = 0, pos2 = 0, pos3 = 0, pos4 = 0;
  elmnt.onmousedown = dragMouseDown;
  elmnt.addEventListener("touchstart", dragMouseDown);
  
  let parent = elmnt.parentNode;
  let positions = [parseInt(parent.getAttribute("data-col")),parseInt(parent.getAttribute("data-row"))];
  
  function dragMouseDown(e) {
	
    e = e || window.event;
    //e.preventDefault();
    // get the mouse cursor position at startup:
    pos3 = getClientX(e);
    pos4 = getClientY(e);
	let rackLetterNumber = parseInt(parent.getAttribute("free"));
	if(rackLetterNumber != null && rackLetterNumber != "true")
		dragRackLetter(rackLetterNumber, e);
	
  }
  
  function dragRackLetter(rackLetterNumber, event) {
	  let rackLetter = document.querySelector('.rack .draggable[position="' + rackLetterNumber + '"] .playable-tile');
	  if(!rackLetter)
	  	return;

	  rackLetter.classList.remove("transparent");
	  let center = getCenter(elmnt);
	  let ev = new CustomEvent('mousedown', {});
	  ev["clientX"] = center[0];
	  ev["clientY"] = center[1];
	  
	  let center2 = getCenter(rackLetter.parentNode);
	  let x = center[0] - center2[0];
	  let y = center[1] - center2[1];
	  rackLetter.parentNode.style.top = (rackLetter.offsetTop + y) + "px";

	  let tileWidth = document.querySelector(".rack .draggable").clientWidth;
	  let leftPx = ((parseInt(rackLetterNumber) + 1) * getRackShift()) + (tileWidth * parseInt(rackLetterNumber)) + 10;



	  rackLetter.parentNode.style.left = (rackLetter.offsetLeft + x + leftPx) + "px";
	  elmnt.classList.add("invisible");
	  parent.setAttribute("free", "true");
	  parent.querySelector(".playable-tile").classList.remove("temporary");
	  rackLetter.parentNode.dispatchEvent(ev);
	  controller.removeTileFromBoard(parent.getAttribute("data-row"), parent.getAttribute("data-col"))
  }
  

  


  
  function elementDrag(e) {
    e = e || window.event;
    e.preventDefault();
  }

  function closeDragElement(e) {
    // stop moving when mouse button is released:
	
    document.onmouseup = null;
    document.onmousemove = null;
  }

  
}


