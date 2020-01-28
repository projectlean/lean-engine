var http = require('http')
var url = require('url')
const D3Node = require('d3-node')
const d3 = require('d3')


const styles = `
.bar rect {
  fill: darkgray;
}
.bar text {
  fill: #fff;
  font: 14px sans-serif;
}`;

var options = {
  styles: styles,
  d3Module: d3
};

function generateHistogram(jBody, requestedWidth, requestedHeight) {

  var component = jBody.component;
  var data = jBody.data;

  console.log("Working with data set from component : " + component);
  console.log("Received data set : " + JSON.stringify(data));

  const d3n = new D3Node(options);


  var formatCount = d3.format(",.0f");

  var margin = {top: 10, right: 30, bottom: 30, left: 30},
      width = requestedWidth - margin.left - margin.right,
      height = requestedHeight - margin.top - margin.bottom;
  var x = d3.scaleLinear()
      .rangeRound([0, width]);

  var bins = d3.histogram()
      .domain(x.domain())
      .thresholds(x.ticks(20))
      (data);

  var y = d3.scaleLinear()
      .domain([0, d3.max(bins, function (d) {
        return d.length;
      })])
      .range([height, 0]);

  const svgWidth = width + margin.left + margin.right

  const svgHeight = height + margin.top + margin.bottom
  var svg = d3n.createSVG(svgWidth, svgHeight)
      .append("g")
      .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

  var bar = svg.selectAll(".bar")
      .data(bins)
      .enter().append("g")
      .attr("class", "bar")
      .attr("transform", function (d) {
        return "translate(" + x(d.x0) + "," + y(d.length) + ")";
      });

  bar.append("rect")
      .attr("x", 1)
      .attr("width", x(bins[0].x1) - x(bins[0].x0) - 1)
      .attr("height", function (d) {
        return height - y(d.length);
      })
      .text(function (d) {

  bar.append("text")
      .attr("dy", ".75em")
      .attr("y", 6)
      .attr("x", (x(bins[0].x1) - x(bins[0].x0)) / 2)
      .attr("text-anchor", "middle")
    return formatCount(d.length);
  });
  svg.append("g")
      .attr("class", "axis axis--x")
      .attr("transform", "translate(0," + height + ")")
      .call(d3.axisBottom(x));

  var imageSvg = d3n.svgString();

  return imageSvg;
}

var server = http.createServer((request, response) => {

  // var data = d3.range(10).map(d3.randomBates(10));

  if (request.method === 'POST') {
    console.log("POST METHOD from " + request.url);

    var body = '';

    request.on('data', function (chunk) {
          // console.log("CHUNK: " + chunk.toString());
          body+=chunk.toString();
        }
    );
    request.on('end', function () {

      // This is the end of processing the stream of JSON data.
      // Now we can parse the JSON and generate the histogram...
      //
      // console.log("Read all data: "+body);
      var jBody = JSON.parse(body);

      // Take the URL apart and get the pieces
      // We use the given width and height to fit into the space
      // calculated by Lean
      //
      var urlParts = url.parse(request.url, true);
      var q = urlParts.query;
      var pWidth = q.width;
      var pHeight = q.height;


      // Generate the histogram, get the SVG
      //
      var svgXml = generateHistogram(jBody, pWidth, pHeight);

      response.writeHead(200, {'Content-Type': 'image/svg+xml'});
      response.end(svgXml);
    });

  } else {

  }

});
server.listen(8080);
