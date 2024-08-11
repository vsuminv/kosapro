/*<![CDATA[*/
var chartData = /*[[${chart}]]*/ {};

var datasets = Object.keys(chartData).map(function(importance) {
    var data = chartData[importance];
    return {
        label: importance,
        data: [
            data['[양호]'] || 0,
            data['[취약]'] || 0,
            data['[N/A]'] || 0
        ],
        backgroundColor: getRandomColor()
    };
});

var ctx = document.getElementById('importanceChart').getContext('2d');
var myChart = new Chart(ctx, {
    type: 'bar',
    data: {
        labels: ['양호', '취약', 'N/A'],
        datasets: datasets
    },
    options: {
        responsive: true,
        scales: {
            y: {
                beginAtZero: true
            }
        }
    }
});

function getRandomColor() {
    var letters = '0123456789ABCDEF';
    var color = '#';
        color += letters[Math.floor(Math.random() * 16)];
    return color;
}
/*]]>*/