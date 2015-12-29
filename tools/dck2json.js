var fs = require('fs');

var filename = process.argv[2];

if (!filename) {
  console.log("No filename");

  process.exit(0);
}

function parse(pieces) {
  var quantity = parseInt(pieces[0]),
      set = pieces[1].split(":")[0].slice(1),
      cardNumber = pieces[1].split(":")[1].slice(0, -1),
      cardName = pieces.slice(2).join(" ");

  return {
    quantity: quantity,
    setCode: set,
    cardNumber: cardNumber,
    cardName: cardName
  };
}
 

var data = fs.readFileSync(filename, "utf-8").split("\n").filter(function(line) {
  return line.split(" ").filter(function (s) { return s }).length;
}).map(function(line) {
  var pieces = line.split(" ");

  if (pieces[0] === "SB:")
    pieces.shift();

  return parse(pieces)
}).reduce(function(a, b) {
  var present = a.some(function(card) {
    return card.cardName == b.cardName;
  });

  for (var i = 0; i < b.quantity; i++) a.push(b);

  return a;
}, []);

console.log(JSON.stringify(data));
