class CanvasPrinter{
    SIZE = 300;
    LINE_WIDTH = 2;
    TEXT_SIZE = 12;
    TEXT_MARGIN = 10;
    TEXT_LINE_HEIGHT = 2;
    COLOR_RED = "#ff0a07"
    COLOR_GREEN = "#00f608"
    COLOR_MAIN = "aqua"
    constructor() {
        this.canvas = document.getElementById("graph");
        this.ctx = this.canvas.getContext("2d");
        this.ctx.font = `${this.TEXT_SIZE}px Roboto`
        this.lastClickedR = null;
    }

    drawStartImage(){
        this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);
        this.drawAxes();
        this.setPointerAtDot(5);
        this.setPointerAtDot(3);
        this.setPointerAtDot(1);
        this.setPointerAtDot(-5);
    }

    redrawAll(r){
        this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);
        this.drawGraph(r);
        this.drawAxes();
        this.setPointerAtDot(5);
        this.setPointerAtDot(3);
        this.setPointerAtDot(1);
        this.setPointerAtDot(-5);
        this.drawPoints();
        updateSquare();
    }

    drawAxes() {
        this.ctx.fillStyle = "black";
        this.drawArrow(-this.SIZE, this.SIZE / 2, this.SIZE, this.SIZE / 2);
        this.drawArrow( this.SIZE / 2, this.SIZE, this.SIZE / 2, 0);
    }

    drawGraph(r){
        const totalPoints = 13;
        const pointInPixels = this.SIZE / totalPoints;
        this.lastClickedR = r;
        this.ctx.fillStyle = this.COLOR_MAIN;

        this.ctx.fillRect(
            this.SIZE / 2,
            this.SIZE / 2,
            r / 2 * pointInPixels,
            - r * pointInPixels
        );

        this.ctx.beginPath();
        this.ctx.moveTo(this.SIZE / 2, this.SIZE / 2);
        this.ctx.lineTo(this.SIZE / 2, this.SIZE / 2 + 2 * (r / 2) * pointInPixels); // Умножаем на 2
        this.ctx.lineTo(this.SIZE / 2 + (r / 2) * pointInPixels, this.SIZE / 2);
        this.ctx.lineTo(this.SIZE / 2, this.SIZE / 2);
        this.ctx.fill();

        this.ctx.beginPath();
        this.ctx.moveTo(this.SIZE / 2, this.SIZE / 2);
        this.ctx.arc(
            this.SIZE / 2,
            this.SIZE / 2,
            r / 2 * pointInPixels,
            -Math.PI / 2,
            -Math.PI,
            true
        );
        this.ctx.lineTo(this.SIZE / 2, this.SIZE / 2);
        this.ctx.closePath();
        this.ctx.fill();
    }

    setPointerAtDot(max_r = 5) {
        const totalPoints = 13;
        const pointInPixels = this.SIZE / totalPoints;
        this.ctx.textAlign = "center";
        this.ctx.textBaseline = "middle";
        this.ctx.fillText(`${max_r}`, this.SIZE / 2 + pointInPixels * max_r, this.SIZE / 2 - this.TEXT_MARGIN);
        this.ctx.fillText(`${max_r}`, this.SIZE / 2 + this.TEXT_MARGIN, this.SIZE / 2 - pointInPixels * max_r);

        this.ctx.beginPath()
        this.ctx.lineWidth = this.LINE_WIDTH;
        this.ctx.moveTo(this.SIZE / 2 + pointInPixels * max_r, this.SIZE / 2 + this.TEXT_LINE_HEIGHT);
        this.ctx.lineTo(this.SIZE / 2 + pointInPixels * max_r, this.SIZE / 2 - this.TEXT_LINE_HEIGHT);
        this.ctx.moveTo(this.SIZE / 2 + this.TEXT_LINE_HEIGHT, this.SIZE / 2 - pointInPixels * max_r);
        this.ctx.lineTo(this.SIZE / 2 - this.TEXT_LINE_HEIGHT, this.SIZE / 2 - pointInPixels * max_r);
        this.ctx.stroke();
    }

    drawArrow(fromx, fromy, tox, toy) {
        var headlen = 10; // length of head in pixels
        var dx = tox - fromx;
        var dy = toy - fromy;
        var angle = Math.atan2(dy, dx);
        this.ctx.beginPath();
        this.ctx.lineWidth = this.LINE_WIDTH;
        this.ctx.moveTo(fromx, fromy);
        this.ctx.lineTo(tox, toy);
        this.ctx.lineTo(tox - headlen * Math.cos(angle - Math.PI / 6), toy - headlen * Math.sin(angle - Math.PI / 6));
        this.ctx.moveTo(tox, toy);
        this.ctx.lineTo(tox - headlen * Math.cos(angle + Math.PI / 6), toy - headlen * Math.sin(angle + Math.PI / 6));
        this.ctx.stroke();
    }

    parseClick(event){
        const totalPoints = 13;
        const xPixels = event.clientX - this.canvas.getBoundingClientRect().left;
        const yPixels = event.clientY - this.canvas.getBoundingClientRect().top;
        const pointInPixels = this.SIZE / totalPoints;
        const x = (- (this.SIZE / 2 - xPixels) / pointInPixels).toFixed(4)
        const y = ((this.SIZE / 2 - yPixels) / pointInPixels).toFixed(4)

        if(x > 4 || x < -4 || y > 3 || y < -5) {
            Swal.fire({
                title: 'Клик вне зоны графика',
                text: 'X принимает значения от -4 до 4\n Y от -5 до 3',
                icon: 'warning'
            });
            return
        }

        if(!this.lastClickedR) {
            Swal.fire({
                title: 'Невозможно определить радиус',
                text: 'Выберите радиус',
                icon: 'warning'
            });
            return
        }

        addAttempt(
            [
                { name: "x", value: x.toString() },
                { name: "y", value: y.toString() },
                { name: "r", value: this.lastClickedR.toString() }
            ]
        )

        updateGraph();
    }

    drawPoints(){
        var arrData=[];
        $(".main-table tr").each(function(){
            let currentRow=$(this);
            arrData.push({
                "x": parseFloat(currentRow.find("td:eq(0)").text()),
                "y": parseFloat(currentRow.find("td:eq(1)").text()),
                "r": parseFloat(currentRow.find("td:eq(2)").text()),
                "status": currentRow.find("td:eq(3)").text() === "true",
                "time": currentRow.find("td:eq(4)").text(),
                "scriptTime": currentRow.find("td:eq(5)").text()
            })
        });
        arrData.shift() // Delete headers
        arrData.forEach(dot =>{
            canvasPrinter.drawPoint(dot.x, dot.y, dot.r, dot.status)
        })
    }

    drawPoint(x, y, r, success = true) {
        const totalPoints = 13;
        let r_now = this.lastClickedR;
        if(r_now != null) {
            x *= r_now / r
            y *= r_now / r

        }
        this.ctx.fillStyle = success
            ? this.COLOR_GREEN
            : this.COLOR_RED;
        const pointInPixels = this.SIZE / totalPoints;
        this.ctx.beginPath();
        this.ctx.arc(
            this.SIZE / 2 + pointInPixels * x,
            this.SIZE / 2 - y * pointInPixels,
            5,
            0,
            Math.PI * 2,
        );
        this.ctx.fill();
        this.ctx.beginPath();
        this.ctx.fillStyle = "black"
        this.ctx.lineWidth = 1.5
        this.ctx.arc(
            this.SIZE / 2 + pointInPixels * x,
            this.SIZE / 2 - y * pointInPixels,
            5,
            0,
            Math.PI * 2
        )
        this.ctx.stroke();
    }
}

function updateSquare() {
    updateCurrentSquare([{ name: "r", value: canvasPrinter.lastClickedR.toString() }]);
}
