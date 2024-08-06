document.addEventListener('DOMContentLoaded', function () {
    const importanceElements = document.querySelectorAll('.importance');
    const statusElements = document.querySelectorAll('.status');
    var transformText = '';

    importanceElements.forEach(element => {
        const importance = element.textContent;
        let bgColor = '';
        let textColor = '';
        let font = '';
        let transformText = '';

        if (importance === '(상)') {
            bgColor='bg-red-500'
            textColor = 'text-gray-500';
            transformText = "dd"
        } else if (importance === '(중)') {
            textColor = 'text-gray-500';
        } else {
            textColor = 'text-grey-500';
        }

        element.parentElement.classList.add(transformText, bgColor, textColor);
    });

    statusElements.forEach(element => {
        const status = element.textContent;
        let bgColor = '';
        let textColor = '';
        let statusText = '';

        if (status === '[취약]') {
            bgColor = 'bg-red-200';
            textColor = 'text-white';
            statusText = 'Warning';
        } else if (status === '[인터뷰]') {
            bgColor = 'bg-yellow-200';
            textColor = 'text-white';
            statusText = 'Interview';
        } else {
            bgColor = 'bg-green-200';
            textColor = 'text-white';
            statusText = 'Good';
        }

        element.parentElement.classList.add(bgColor, textColor);
        element.textContent = statusText;
    });

});

