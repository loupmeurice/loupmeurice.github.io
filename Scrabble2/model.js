const MT = "MT";
const LD = "LD";
const LT = "LT";
const MD = "MD";
const SCRABBLE_PRIME = 50;
const SCRABBLE_LENGTH = 7;
class Model {
	row_length = 15;
	column_length = 15;
	center = [7,7];
	nb_of_rack_riles = 7;
	rack = [];
	myScore = 0;
	myLastPlay = null;
	masterScore = 0;
	masterLastPlay = null;
	numberOfPlays = 0;
	myPlays = [];
	myRacks = [];


	constructor() {
	  	this.board = [];
	  	this.play = [];
	  	this.rack = [];
	  	for(let i = 0; i < this.row_length; i++) {
			let arr = [];
			let arr2 = [];
			for(let j = 0; j < this.column_length; j++) {
				arr.push(null);
				arr2.push(null);
			}
			this.board.push(arr);
			this.play.push(arr2);
	  	}

	  	//for(const arr of this.randomTiles(45)) {
	  	//	this.board[arr[0]][arr[1]] = this.randomLetter()
		//}

		/*this.board[14][3] = "E";
		this.board[13][3] = "E";
		this.board[12][3] = "R";
		this.board[11][3] = "O";
		this.board[11][4] = "S";
		this.board[10][4] = "A";
		this.board[9][4] = "D";
		this.board[8][4] = "A";
		this.board[7][4] = "D";
		this.board[9][6] = "T";
		this.board[9][7] = "E";
		this.board[9][8] = "S";
		this.board[9][9] = "T";
		this.board[9][10] = "E";
		this.board[9][11] = "E";
		this.board[9][12] = "S";
		this.board[8][5] = "N";
		this.board[8][6] = "E";
		this.board[7][5] = "E";
		this.board[7][6] = "J";
		this.board[7][7] = "A";

		this.rack = ["F", "I", "L", "R"];
		this.tiles = [];*/
		//this.rack = ["L", "I", "S", "E", "U", "S", "E"];

		//this.tiles = ["A", "B", "C", "D", "E", "E"];
		this.rack = ["I", "F", "C", "F", "O", "R", "E"];
		this.tiles = [];

	}

	addRack() {
		this.myRacks.push([...this.rack]);
	}

	addPlay(myPlay, masterPlay) {
		this.myScore += myPlay;
		this.masterScore += masterPlay.points;
		this.numberOfPlays++;
		this.myLastPlay = myPlay;
		this.masterLastPlay = masterPlay.points;
		this.myPlays.push(myPlay);
		this.addRack();
	}

	getNumberOfRemainingLetters() {
		return this.tiles.length;
	}

	randomTiles(number) {
		let res = [];
		let array = [];
		for(let i = 0; i < 15; i++)
			for(let j = 0; j < 15; j++)
				array.push([i, j]);
		for(let i = 0; i < number; i++) {
			let index = Math.floor(Math.random() * array.length);
			res.push(array[index]);
			array.splice(index, 1);
		}

		return res;
	}

	randomLetter() {
		let characters       = 'abcdefghijklmnopqrstuvwxyz';
		let charactersLength = characters.length;
		return characters.charAt(Math.floor(Math.random() * charactersLength));
	}


	getPlayedLetters() {
		let res = [];
		for(let i = 0; i < this.play.length; i++) {
			let row = this.play[i];
			row.forEach(function (item, index) {
				if(item != null)
					res.push(item.toUpperCase());
			});
		}
		return res;
	}

	getPlayedRows() {
		let rows = [];
		for(let i = 0; i < this.play.length; i++) {
			let row = this.play[i];
			let ok = false;
			row.forEach(function (item, index) {
				if(item != null)
					ok = true;
			});

			if(ok == true)
				rows.push(i);
		}

		return rows;
	}

	getPlayedColumns() {
		let columns = [];
		for(let i = 0; i < this.column_length; i++) {
			let ok = false;

			for(let j = 0; j < this.row_length; j++) {
				if(this.play[j][i] != null) {
					ok = true;
					break;
				}
			}

			if(ok == true)
				columns.push(i);
		}
		return columns;
	}

	add(i, j, l) {
		this.play[i][j] = l;
	}

	removeAllTilesFromBoard() {
		for(let i = 0; i < this.row_length; i++)
			for(let j = 0; j < this.column_length; j++)
				this.remove(i, j);
	}

	remove(i, j) {
		this.play[i][j] = null;
	}

	isEmptyBoard() {
		for(let i = 0; i < this.row_length; i++)
			for(let j = 0;  j < this.column_length; j++)
				if(this.board[i][j] != null)
					return false;
		return true;
	}



	tiles = ['A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'B', 'B', 'C', 'C', 'D', 'D', 'D', 'E', 'E', 'E', 'E', 'E', 'E', 'E', 'E', 'E', 'E', 'E', 'E', 'E', 'E', 'E', 'F', 'F', 'G', 'G', 'H', 'H', 'I', 'I', 'I', 'I', 'I', 'I', 'I', 'I', 'J', 'K', 'L', 'L', 'L', 'L', 'L', 'M', 'M', 'M', 'N', 'N', 'N', 'N', 'N', 'N', 'O', 'O', 'O', 'O', 'O', 'O', 'P', 'P', 'Q', 'R', 'R', 'R', 'R', 'R', 'R', 'S', 'S', 'S', 'S', 'S', 'S', 'T', 'T', 'T', 'T', 'T', 'T', 'U', 'U', 'U', 'U', 'U', 'U', 'V', 'V', 'W', 'X', 'Y', 'Z'];
	points = new Map([
		{ key: 'A', value: 1 },
		{ key: 'B', value: 3 },
		{ key: 'C', value: 3 },
		{ key: 'D', value: 2 },
		{ key: 'E', value: 1 },
		{ key: 'F', value: 4 },
		{ key: 'G', value: 2 },
		{ key: 'H', value: 4 },
		{ key: 'I', value: 1 },
		{ key: 'J', value: 8 },
		{ key: 'K', value: 10 },
		{ key: 'L', value: 1 },
		{ key: 'M', value: 2 },
		{ key: 'N', value: 1 },
		{ key: 'O', value: 1 },
		{ key: 'P', value: 3 },
		{ key: 'Q', value: 8 },
		{ key: 'R', value: 1 },
		{ key: 'S', value: 1 },
		{ key: 'T', value: 1 },
		{ key: 'U', value: 1 },
		{ key: 'V', value: 4 },
		{ key: 'W', value: 10 },
		{ key: 'X', value: 10 },
		{ key: 'Y', value: 10},
		{ key: 'Z', value: 10},
		{ key: ' ', value: 0}
	].map((obj) => [obj.key, obj.value]));

	bonus = [[MT, null, null, LD, null, null, null, MT, null, null, null, LD, null, null, MT],
			 [null, MD, null, null, null, LT, null, null, null, LT, null, null, null, MD, null],
		     [null, null, MD, null, null, null, LD, null, LD, null, null, null, MD, null, null],
			 [LD, null, null, MD, null, null, null, LD, null, null, null, MD, null, null, LD],
			 [null, null, null, null, MD, null, null, null, null, null, MD, null, null, null, null],
			 [null, LT, null, null, null, LT, null, null, null, LT, null, null, null, LT, null],
			 [null, null, LD, null, null, null, LD, null, LD, null, null, null, LD, null, null],
			 [MT, null, null, LD, null, null, null, MD, null, null, null, LD, null, null, MT],
			 [null, null, LD, null, null, null, LD, null, LD, null, null, null, LD, null, null],
			 [null, LT, null, null, null, LT, null, null, null, LT, null, null, null, LT, null],
			 [null, null, null, null, MD, null, null, null, null, null, MD, null, null, null, null],
			 [LD, null, null, MD, null, null, null, LD, null, null, null, MD, null, null, LD],
			 [null, null, MD, null, null, null, LD, null, LD, null, null, null, MD, null, null],
			 [null, MD, null, null, null, LT, null, null, null, LT, null, null, null, MD, null],
			 [MT, null, null, LD, null, null, null, MT, null, null, null, LD, null, null, MT]];

	getLetterPoint(letter) {
		return this.points.get(letter.toUpperCase());
	}

	getBonus(coordinates) {
		let row_num = coordinates[0];
		let col_num = coordinates[1];

		return this.bonus[row_num][col_num];
	}
}




