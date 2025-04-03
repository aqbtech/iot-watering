import {Box, Button, Typography} from '@mui/material'
import {useEffect, useRef, useState} from 'react'
import WaterDropOutlinedIcon from '@mui/icons-material/WaterDropOutlined'
import WbSunnyOutlinedIcon from '@mui/icons-material/WbSunnyOutlined'
import SpaOutlinedIcon from '@mui/icons-material/SpaOutlined'
import ThermostatOutlinedIcon from '@mui/icons-material/ThermostatOutlined'
import DataFrame from '../../components/dataFrame'
import {getLatestDataAPI} from '../../apis/deviceApi'
import {BASE_URL} from "../../util/constant.js";

const DataField = ({deviceId}) => {
    const [data, setData] = useState({
        updatedTime: '1747221060',
        wateringTime: '--',
        Temperature: '--',
        soilMoisture: '--',
        Light: '--',
        Humidity: '--'
    })
    const [error, setError] = useState(false) // Trạng thái lỗi

    useEffect(() => {
        let eventSource;

        const connect = () => {
            eventSource = new EventSource(`${BASE_URL}/device/v1/events?deviceId=${deviceId}`);

            eventSource.onmessage = (event) => {
                console.log("Received data:", event);
                setData(JSON.parse(event.data));
            };

            eventSource.onerror = (error) => {
                console.error("SSE error:", error);
                eventSource.close();
            };
        };

        setTimeout(() => {
            console.log("Mở connection SSE...");
            connect();
        }, 2000);

        return () => {
            console.log("Đóng connection SSE trước khi unmount...");
            eventSource?.close();
        };
    }, [deviceId]);


    const formatTime = (timestamp) => {
        if (!timestamp) return 'Đang cập nhật...'

        let date
        if (!isNaN(timestamp)) {
            // Nếu timestamp là UNIX timestamp (số), cần nhân 1000 để chuyển từ giây -> ms
            date = new Date(Number(timestamp) * 1000)
        } else {
            // Nếu timestamp là dạng chuỗi ISO 8601, dùng Date trực tiếp
            date = new Date(timestamp)
        }

        return date.toLocaleString('en-US', {
            weekday: 'long', // Thứ mấy (Ví dụ: "Thứ Hai")
            day: '2-digit',
            month: '2-digit',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit',
            second: '2-digit',
            hour12: false
        })
    }

    const sensorData = [
        {label: 'Temperature', value: data ? `${data.Temperature} °C` : '...', icon: ThermostatOutlinedIcon},
        {label: 'Humidity', value: data ? `${data.Humidity} %` : '...', icon: WaterDropOutlinedIcon},
        {label: 'Light', value: data ? `${data.Light} Lux` : '...', icon: WbSunnyOutlinedIcon},
        {label: 'Soil Moisture', value: data ? `${data.soilMoisture} %` : '...', icon: SpaOutlinedIcon}
    ]

    return (
        <Box sx={{display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 2, mt: 2}}>
            <Box sx={{
                display: 'grid',
                gap: 2,
                width: '100%',
                gridTemplateColumns: {
                    xs: 'repeat(auto-fit, minmax(140px, 1fr))',
                    md: 'repeat(auto-fit, minmax(200px, 1fr))'
                }
            }}>
                {sensorData.map((item, index) => (
                    <DataFrame name={item.label} value={item.value} icon={item.icon} key={index}/>
                ))}
            </Box>
            <Typography variant="subtitle1" sx={{color: 'gray'}}>
                Updated: {formatTime(data.updatedTime)}
            </Typography>
        </Box>

    )
}

export default DataField
